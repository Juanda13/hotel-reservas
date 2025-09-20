package com.juanda.backend.web.dto;

import java.util.List;

public record SearchResponseDTO(
    String checkIn,
    String checkOut,
    int guests,
    List<SearchResultDTO> results,
    long total,         // total de resultados (antes de paginar)
    Integer page,       // página solicitada (opcional)
    Integer size,       // tamaño de página (opcional)
    String sort         // ej: "price,asc" (opcional)
) {
}
