package com.juanda.backend.web.dto;

import java.math.BigDecimal;
import java.util.Map;

public record SearchResultDTO(
    Long roomId,
    String code,
    String type,
    Integer capacity,
    BigDecimal price,
    Map<String, Object> amenities
) {
}
