package com.juanda.backend.service;

import com.juanda.backend.domain.Reservation;
import com.juanda.backend.domain.Room;
import com.juanda.backend.domain.RoomInventory;
import com.juanda.backend.repository.ReservationRepository;
import com.juanda.backend.repository.RoomInventoryRepository;
import com.juanda.backend.repository.RoomRepository;
import com.juanda.backend.web.dto.BookingRequestDTO;
import com.juanda.backend.web.dto.ReservationDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;

@Service
public class BookingService {

    private final RoomRepository roomRepo;
    private final RoomInventoryRepository invRepo;
    private final ReservationRepository resRepo;

    private final SecureRandom random = new SecureRandom();

    public BookingService(RoomRepository roomRepo, RoomInventoryRepository invRepo, ReservationRepository resRepo) {
        this.roomRepo = roomRepo;
        this.invRepo = invRepo;
        this.resRepo = resRepo;
    }

    /**
     * Crea una reserva de forma atómica:
     * - Valida rango/capacidad
     * - Bloquea (PESSIMISTIC_WRITE) noches del rango
     * - Marca noches como reservadas
     * - Persiste la Reservation
     * - Limpia el caché de /api/search
     */
    @Transactional
    @CacheEvict(value = "search", allEntries = true)
    public ReservationDTO book(BookingRequestDTO req) {
        LocalDate ci = req.checkIn();
        LocalDate co = req.checkOut();

        if (ci == null || co == null || !co.isAfter(ci)) {
            throw new IllegalArgumentException("Rango de fechas inválido (checkOut debe ser > checkIn).");
        }
        if (req.guests() < 1) {
            throw new IllegalArgumentException("Guests debe ser >= 1.");
        }

        Room room = roomRepo.findById(req.roomId())
            .orElseThrow(() -> new IllegalArgumentException("La habitación no existe."));
        if (room.getCapacity() != null && room.getCapacity() < req.guests()) {
            throw new IllegalStateException("Guests excede la capacidad de la habitación.");
        }

        int nights = (int) ChronoUnit.DAYS.between(ci, co);

        // BLOQUEO del rango: evita doble-reserva concurrente
        List<RoomInventory> nightsInv = invRepo.lockRangeForUpdate(room.getId(), ci, co);
        if (nightsInv.size() != nights) {
            throw new IllegalStateException("El rango no está completamente cargado en inventario.");
        }

        // Validar disponibilidad (ni bloqueadas ni reservadas)
        boolean conflict = nightsInv.stream().anyMatch(ri -> ri.isBlocked() || ri.isBooked());
        if (conflict) {
            throw new IllegalStateException("Noches no disponibles para este rango.");
        }

        // Calcular total y marcar como reservadas
        BigDecimal total = BigDecimal.ZERO;
        for (RoomInventory ri : nightsInv) {
            total = total.add(ri.getBasePrice());
            ri.setBooked(true);
        }
        invRepo.saveAll(nightsInv);

        // Crear Reservation
        Reservation r = new Reservation();
        r.setCode(generatedCode());
        r.setRoom(room);
        r.setCheckIn(ci);
        r.setCheckOut(co);
        r.setGuests(req.guests());
        r.setTotal(total);
        r.setStatus("CONFIRMED");
        r.setCustomerName(req.customerName());
        r.setCustomerEmail(req.customerEmail());
        r.setCustomerPhone(req.customerPhone());
        r = resRepo.save(r);

        return new ReservationDTO(
            r.getCode(),
            room.getId(),
            room.getCode(),
            r.getCheckIn(),
            r.getCheckOut(),
            r.getGuests(),
            r.getTotal(),
            r.getStatus()
        );
    }

    private String generatedCode() {
        byte[] buf = new byte[8]; // 16 chars hex
        random.nextBytes(buf);
        return HexFormat.of().withUpperCase().formatHex(buf);
    }

}
