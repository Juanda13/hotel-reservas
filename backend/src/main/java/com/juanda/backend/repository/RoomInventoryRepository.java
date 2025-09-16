package com.juanda.backend.repository;

import com.juanda.backend.domain.RoomInventory;
import com.juanda.backend.repository.projection.RoomAvailabilityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    @Query(value = """
        SELECT r.id   AS room_id,
               r.code AS code,
               r.type AS type,
               r.capacity AS capacity,
               r.amenities::text AS amenities_json,
               SUM(ri.base_price) AS total_price
        FROM room r
        JOIN room_inventory ri ON ri.room_id = r.id
        WHERE r.capacity >= :guests
          AND ri.date >= :checkIn
          AND ri.date < :checkOut
          AND ri.is_blocked = FALSE
        GROUP BY r.id, r.code, r.type, r.capacity, r.amenities
        HAVING COUNT(ri.id) = :nights
        ORDER BY total_price ASC
        """, nativeQuery = true)
    List<RoomAvailabilityProjection> findAvailableRooms(
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("guests") int guests,
        @Param("nights") int nights
    );

}
