package com.juanda.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "room_inventory",
    uniqueConstraints = @UniqueConstraint(name = "uq_room_date", columnNames = {"room_id", "date"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoomInventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "is_blocked", nullable = false)
    private boolean blocked = false;

    @Column(name = "is_booked", nullable = false)
    private boolean booked = false;

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
