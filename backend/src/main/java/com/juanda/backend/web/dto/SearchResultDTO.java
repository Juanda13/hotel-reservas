package com.juanda.backend.web.dto;

import java.math.BigDecimal;

public record SearchResultDTO(
    Long roomId,
    String type,
    Integer capacity,
    BigDecimal price
) {
}
