package com.juanda.backend.repository.projection;

import java.math.BigDecimal;

public interface RoomAvailabilityProjection {

    Long getRoomId();
    String getType();
    Integer getCapacity();
    BigDecimal getTotalPrice();

}
