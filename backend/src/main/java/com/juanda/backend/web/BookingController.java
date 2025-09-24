package com.juanda.backend.web;

import com.juanda.backend.service.BookingService;
import com.juanda.backend.web.dto.BookingRequestDTO;
import com.juanda.backend.web.dto.ReservationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear reserva", description = "Confirma una reserva para una habitación y rango")
    @ApiResponse(responseCode = "201", description = "Reserva creada")
    public ReservationDTO create(@Valid @RequestBody BookingRequestDTO req) {
        return service.book(req);
    }

}
