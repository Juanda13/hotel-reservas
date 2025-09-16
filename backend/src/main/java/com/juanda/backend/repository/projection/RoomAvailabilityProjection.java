package com.juanda.backend.repository.projection;

import java.math.BigDecimal;

public interface RoomAvailabilityProjection {

    Long getRoomId();
    String getCode();
    String getType();
    Integer getCapacity();
    BigDecimal getTotalPrice();
    String getAmenitiesJson();

}
