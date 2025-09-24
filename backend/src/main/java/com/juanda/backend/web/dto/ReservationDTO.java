package com.juanda.backend.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Reservar confirmada")
public record ReservationDTO(

    @Schema(example = "9F2A7C3D8B12A4EF", description = "Código único de la reserva")
    String code,

    @Schema(example = "1")
    Long roomId,

    @Schema(example = "R101")
    String roomCode,

    @Schema(example = "2025-09-20")
    LocalDate checkIn,

    @Schema(example = "2025-09-22")
    LocalDate checkOut,

    @Schema(example = "2")
    int guests,

    @Schema(example = "180.00")
    BigDecimal total,

    @Schema(example = "CONFIRMED")
    String status
) {
}
