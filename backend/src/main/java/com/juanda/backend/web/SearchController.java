package com.juanda.backend.web;

import com.juanda.backend.web.error.ApiValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Resultados encontrados",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                name = "OK",
                value = """
              {
                "checkIn": "2025-09-15",
                "checkOut": "2025-09-18",
                "guests": 2,
                "results": [
                  {"roomId":101,"type":"double","capacity":2,"price":120.0},
                  {"roomId":203,"type":"suite","capacity":3,"price":240.0}
                ]
              }
              """
            ))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parámetros inválidos",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                name = "Bad Request",
                value = """
              {
                "timestamp": "2025-09-10T05:10:00Z",
                "status": 400,
                "error": "Bad Request",
                "message": "checkOut debe ser posterior a checkIn y guests >= 1",
                "path": "/api/search"
              }
              """
            ))
        )
    })
    public ResponseEntity<Map<String, Object>> search(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-in (YYYY-MM-DD)", example = "2025-09-15")
        LocalDate checkIn,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Fecha de check-out (YYYY-MM-DD)", example = "2025-09-18")
        LocalDate checkOut,

        @RequestParam
        @Parameter(description = "Número de huéspedes (>= 1)", example = "2")
        int guests
    ) {
        // Validaciones simples (por ahora)
        if (!checkOut.isAfter(checkIn) || guests < 1) {
            throw new ApiValidationException("checkOut debe ser posterior a checkIn y guests >= 1");
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
