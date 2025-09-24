package com.juanda.backend.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Solicitud para crear una reserva")
public record BookingRequestDTO(

    @Schema(example = "1", description = "ID de la habitación a reservar")
    Long roomId,

    @Schema(example = "2025-09-20")
    LocalDate checkIn,

    @Schema(example = "2025-09-22")
    LocalDate checkOut,

    @Schema(example = "2", minimum = "1")
    int guests,

    @Schema(example = "Juan Pérez")
    String customerName,

    @Schema(example = "juan@example.com")
    String customerEmail,

    @Schema(example = "+57 300 000 0000")
    String customerPhone
) {
}
