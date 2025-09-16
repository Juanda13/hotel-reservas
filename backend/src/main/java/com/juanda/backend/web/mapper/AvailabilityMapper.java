package com.juanda.backend.web.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanda.backend.repository.projection.RoomAvailabilityProjection;
import com.juanda.backend.web.dto.SearchResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "price", source = "totalPrice")
    @Mapping(target = "amenities", expression = "java(parseAmenities(p.getAmenitiesJson()))")
    SearchResultDTO toDto(RoomAvailabilityProjection p);

    List<SearchResultDTO> toDtoList(List<RoomAvailabilityProjection> list);

    // Reutilizable ObjectMapper (Jackson recomienda reutilizarlo, es thread-safe)
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Helper para parsear JSON -> Map usando Jackson
    default Map<String, Object> parseAmenities(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of(); // En caso de JSON inválido
        }
    }

}
