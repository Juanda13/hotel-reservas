package com.juanda.backend.service;

import com.juanda.backend.repository.RoomInventoryRepository;
import com.juanda.backend.web.dto.SearchResponseDTO;
import com.juanda.backend.web.error.ApiValidationException;
import com.juanda.backend.web.mapper.AvailabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class SearchService {

    private final RoomInventoryRepository inventoryRepo;
    private final AvailabilityMapper mapper;

    public SearchService(RoomInventoryRepository inventoryRepo, AvailabilityMapper mapper) {
        this.inventoryRepo = inventoryRepo;
        this.mapper = mapper;
    }

    @Cacheable(
        value = "search",
        key = "T(java.lang.String).format('%s|%s|%s', #checkIn, #checkOut, #guests)"
    )
    public SearchResponseDTO search(LocalDate checkIn, LocalDate checkOut, int guests) {
        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        var list = inventoryRepo.findAvailableRooms(checkIn, checkOut, guests, nights);
        var dtos = mapper.toDtoList(list);

        return new SearchResponseDTO(
            checkIn, checkOut, guests,
            dtos,
            dtos.size(),   // total
            null,          // page (controller lo llenará si pagina)
            null,          // size
            null           // sort
        );
    }

}
