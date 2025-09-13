package com.juanda.backend.repository;

import com.juanda.backend.domain.RoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {
}
