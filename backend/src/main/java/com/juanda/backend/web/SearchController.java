package com.juanda.backend.web;

import com.juanda.backend.service.SearchService;
import com.juanda.backend.web.dto.SearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    @Operation(summary = "Buscar disponibilidad real")
    public ResponseEntity<SearchResponseDTO> search(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-in (YYYY-MM-DD)", example = "2025-09-15")
        LocalDate checkIn,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-out (YYYY-MM-DD)", example = "2025-09-18")
        LocalDate checkOut,
        @RequestParam
        @Parameter(description = "Número de huéspedes (>=1)", example = "2")
        int guests
    ) {
        return ResponseEntity.ok(searchService.search(checkIn, checkOut, guests));
    }

}
