package com.juanda.backend.web.dto;

import java.util.List;

public record SearchResponseDTO(
    String checkIn,
    String checkOut,
    int guests,
    List<SearchResultDTO> results
) {
}
