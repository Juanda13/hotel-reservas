package com.juanda.backend.repository;

import com.juanda.backend.domain.Room;
import com.juanda.backend.repository.projection.RoomAvailabilityProjection;
import com.juanda.backend.testinfra.PostgresTC;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
class RoomInventoryRepositoryIT extends PostgresTC {

    @Autowired
    RoomInventoryRepository inventoryRepo;

    @Autowired
    RoomRepository roomRepo;

    @Test
    void should_find_available_rooms_for_range_and_guests() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(3); // 2 noches
        int guests = 2;
        int nights = 2;

        List<RoomAvailabilityProjection> list =
            inventoryRepo.findAvailableRooms(checkIn, checkOut, guests, nights);

        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(list).allSatisfy(p -> {
            Assertions.assertThat(p.getCapacity()).isGreaterThanOrEqualTo(guests);
            Assertions.assertThat(p.getTotalPrice()).isPositive();
        });
    }

    @Test
    void should_exclude_blocked_room_R201_when_range_includes_blocked_day_plus3() {
        // En V2__seed.sql bloqueamos R201 en CURRENT_DATE + 3
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(1); // ✅ 1 noche: sólo día +3
        int guests = 2;
        int nights = 1;

        var list = inventoryRepo.findAvailableRooms(checkIn, checkOut, guests, nights);

        Map<Long, String> codes = roomRepo.findAll().stream()
            .collect(Collectors.toMap(Room::getId, Room::getCode));

        // R201 NO debe salir
        Assertions.assertThat(list)
            .noneSatisfy(p -> Assertions.assertThat(codes.get(p.getRoomId())).isEqualTo("R201"));
    }

}
