package com.juanda.backend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @GetMapping
    @Operation(
        summary = "Buscar disponibilidad (mock)",
        description = "Devuelve resultados de ejemplo para el rango de fechas y número de huéspedes."
    )
    public ResponseEntity<Map<String, Object>> search(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-in (YYYY-MM-DD)")
        LocalDate checkIn,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-out (YYYY-MM-DD)")
        LocalDate checkOut,

        @RequestParam
        @Parameter(description = "Número de huéspedes (>= 1)")
        int guests
    ) {
        // Validaciones simples (por ahora)
        if (!checkOut.isAfter(checkIn) || guests < 1) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Parámetros inválidos: checkOut debe ser posterior a checkIn y guests >= 1")
            );
        }

        // MOCK (luego conectaremos con inventario real + precios)
        List<Map<String, Object>> rooms = List.of(
            Map.of("roomId", 101, "type", "double", "capacity", 2, "price", 120.00),
            Map.of("roomId", 203, "type", "suite", "capacity", 3, "price", 240.00)
        );

        Map<String, Object> body = Map.of(
            "checkIn", checkIn.toString(),
            "checkOut", checkOut.toString(),
            "guests", guests,
            "results", rooms
        );

        return ResponseEntity.ok(body);
    }

}
