package com.juanda.backend.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Solicitud para crear una reserva")
public record BookingRequestDTO(

    @NotNull
    @Schema(example = "1", description = "ID de la habitación a reservar")
    Long roomId,

    @NotNull
    // Podrías usar @FutureOrPresent si quieres forzar fechas >= hoy
    @Schema(example = "2025-09-20")
    LocalDate checkIn,

    @NotNull
    // Podrías usar @Future si quieres forzar > hoy (la regla checkOut > checkIn la validaremos en el service)
    @Schema(example = "2025-09-22")
    LocalDate checkOut,

    @Min(1)
    @Schema(example = "2", minimum = "1")
    int guests,

    @NotBlank
    @Size(max = 100)
    @Schema(example = "Juan Pérez")
    String customerName,

    @Email
    @Size(max = 255)
    @Schema(example = "juan@example.com")
    String customerEmail,

    @Size(max = 30)
    @Schema(example = "+57 300 000 0000")
    String customerPhone
) {
}
