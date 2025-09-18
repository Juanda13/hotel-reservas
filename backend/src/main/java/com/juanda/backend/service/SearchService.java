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
@RequiredArgsConstructor
public class SearchService {

    private final RoomInventoryRepository inventoryRepo;
    private final AvailabilityMapper mapper;

    @Cacheable(
        value = "search",
        key = "T(java.lang.String).format('%s|%s|%s', #checkIn, #checkOut, #guests)"
    )
    public SearchResponseDTO search(LocalDate checkIn, LocalDate checkOut, int guests) {

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn) || guests < 1) {
            throw new ApiValidationException("checkOut debe ser posterior a checkIn y guests >= 1");
        }

        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        var projections = inventoryRepo.findAvailableRooms(checkIn, checkOut, guests, nights);
        var results = mapper.toDtoList(projections);
        return new SearchResponseDTO(checkIn.toString(), checkOut.toString(), guests, results);
    }

}
