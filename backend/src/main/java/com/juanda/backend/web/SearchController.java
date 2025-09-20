package com.juanda.backend.web;

import com.juanda.backend.service.SearchService;
import com.juanda.backend.web.dto.SearchResponseDTO;
import com.juanda.backend.web.dto.SearchResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    @Operation(summary = "Buscar disponibilidad por rango de fechas y huéspedes",
        description = "Soporta paginación opcional (page,size) y orden (sort=campo,direccion). Campos: price|capacity|type|code")
    public SearchResponseDTO search(
        @RequestParam LocalDate checkIn,
        @RequestParam LocalDate checkOut,
        @RequestParam int guests,
        @Parameter(description = "Página (0..N). Si no se envía, no pagina.")
        @RequestParam(required = false) Integer page,
        @Parameter(description = "Tamaño de página (1..50). Si no se envía, no pagina.")
        @RequestParam(required = false) Integer size,
        @Parameter(description = "Formato campo,direccion. Campos: price|capacity|type|code; Dirección: asc|desc. Ej: price,asc")
        @RequestParam(required = false) String sort
    ) {
        // Respuesta base cacheada (lista completa)
        SearchResponseDTO base = searchService.search(checkIn, checkOut, guests);
        List<SearchResultDTO> list = base.results();

        // Orden
        String effectiveSort = (sort == null || sort.isBlank()) ? "price,asc" : sort.trim().toLowerCase();
        Comparator<SearchResultDTO> comparator = resolveComparator(effectiveSort);
        list = list.stream().sorted(comparator).toList();

        // Si no piden paginación, devolvemos TODO con metadatos básicos
        if (page == null || size == null) {
            return new SearchResponseDTO(
                checkIn, checkOut, guests,
                list,
                list.size(),
                null, null, effectiveSort
            );
        }

        // Validaciones de paginación
        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 50);

        int total = list.size();
        int from = p * s;
        if (from >= total) {
            // página fuera de rango → lista vacía
            return new SearchResponseDTO(
                checkIn, checkOut, guests,
                List.of(),
                total,
                p, s, effectiveSort
            );
        }
        int to = Math.min(from + s, total);
        List<SearchResultDTO> slice = list.subList(from, to);

        return new SearchResponseDTO(
            checkIn, checkOut, guests,
            slice,
            total,
            p, s, effectiveSort
        );
    }

    private Comparator<SearchResultDTO> resolveComparator(String sort) {
        // parse "campo,direccion"
        String[] parts = sort.split(",", 2);
        String field = parts.length > 0 ? parts[0].trim() : "price";
        String dir = parts.length > 1 ? parts[1].trim() : "asc";

        Comparator<SearchResultDTO> cmp;
        switch (field) {
            case "capacity" -> cmp = Comparator.comparing(SearchResultDTO::capacity, Comparator.nullsLast(Integer::compareTo));
            case "type"     -> cmp = Comparator.comparing(SearchResultDTO::type, String.CASE_INSENSITIVE_ORDER);
            case "code"     -> cmp = Comparator.comparing(SearchResultDTO::code, String.CASE_INSENSITIVE_ORDER);
            case "price"    -> cmp = Comparator.comparing(SearchResultDTO::price); // BigDecimal asc
            default         -> cmp = Comparator.comparing(SearchResultDTO::price); // fallback
        }
        if ("desc".equalsIgnoreCase(dir)) {
            cmp = cmp.reversed();
        }
        return cmp;
    }

}
