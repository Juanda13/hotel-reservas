package com.juanda.backend.web.dto;

import java.time.LocalDate;
import java.util.List;

public record SearchResponseDTO(
    LocalDate checkIn,
    LocalDate checkOut,
    int guests,
    List<SearchResultDTO> results,
    long total,         // total de resultados (antes de paginar)
    Integer page,       // página solicitada (opcional)
    Integer size,       // tamaño de página (opcional)
    String sort         // ej: "price,asc" (opcional)
) {
}
