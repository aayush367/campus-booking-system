package com.campus.campusbookingsystem.repository;

import com.campus.campusbookingsystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Room> findByActiveTrueOrderByNameAsc();

    List<Room> findByTypeIgnoreCaseOrderByNameAsc(String type);

    List<Room> findByCapacityGreaterThanEqualOrderByCapacityAsc(
            Integer capacity
    );
}