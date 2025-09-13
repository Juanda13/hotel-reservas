package com.juanda.backend.web.mapper;

import com.juanda.backend.repository.projection.RoomAvailabilityProjection;
import com.juanda.backend.web.dto.SearchResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    @Mapping(target = "price", source = "totalPrice")
    SearchResultDTO toDto(RoomAvailabilityProjection p);
    
    List<SearchResultDTO> toDtoList(List<RoomAvailabilityProjection> list);

}
