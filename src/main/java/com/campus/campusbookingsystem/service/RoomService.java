package com.campus.campusbookingsystem.service;

import com.campus.campusbookingsystem.entity.Room;
import com.campus.campusbookingsystem.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(
            RoomRepository roomRepository) {

        this.roomRepository = roomRepository;
    }


    // =====================================================
    // GET ALL ROOMS
    // =====================================================

    public List<Room> getAllRooms() {

        return roomRepository.findAll();
    }


    // =====================================================
    // GET ACTIVE ROOMS
    // Used by Student / Faculty booking forms
    // =====================================================

    public List<Room> getActiveRooms() {

        return roomRepository
                .findByActiveTrueOrderByNameAsc();
    }


    // =====================================================
    // GET ROOM BY ID
    // =====================================================

    public Room getRoomById(
            Long id) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Room ID is required."
            );
        }

        return roomRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Room not found."
                        )
                );
    }


    // =====================================================
    // CREATE ROOM
    // =====================================================

    @Transactional
    public Room createRoom(
            Room room) {

        if (room == null) {

            throw new IllegalArgumentException(
                    "Room information is required."
            );
        }

        if (isBlank(room.getName())) {

            throw new IllegalArgumentException(
                    "Room name is required."
            );
        }

        if (isBlank(room.getType())) {

            throw new IllegalArgumentException(
                    "Room type is required."
            );
        }

        if (room.getCapacity() == null
                || room.getCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "Room capacity must be greater than 0."
            );
        }

        String cleanedName =
                room.getName().trim();

        if (roomRepository
                .existsByNameIgnoreCase(
                        cleanedName
                )) {

            throw new IllegalArgumentException(
                    "A room with this name already exists."
            );
        }

        room.setName(
                cleanedName
        );

        room.setType(
                room.getType().trim()
        );

        if (room.getBuilding() != null) {

            room.setBuilding(
                    room.getBuilding().trim()
            );
        }

        if (room.getEquipment() != null) {

            room.setEquipment(
                    room.getEquipment().trim()
            );
        }

        if (room.getActive() == null) {

            room.setActive(true);
        }

        return roomRepository.save(
                room
        );
    }


    // =====================================================
    // UPDATE ROOM
    // =====================================================

    @Transactional
    public Room updateRoom(
            Long id,
            Room updatedRoom) {

        Room room =
                getRoomById(id);

        if (isBlank(updatedRoom.getName())) {

            throw new IllegalArgumentException(
                    "Room name is required."
            );
        }

        if (isBlank(updatedRoom.getType())) {

            throw new IllegalArgumentException(
                    "Room type is required."
            );
        }

        if (updatedRoom.getCapacity() == null
                || updatedRoom.getCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "Room capacity must be greater than 0."
            );
        }

        room.setName(
                updatedRoom.getName().trim()
        );

        room.setType(
                updatedRoom.getType().trim()
        );

        room.setCapacity(
                updatedRoom.getCapacity()
        );

        room.setBuilding(
                updatedRoom.getBuilding()
        );

        room.setEquipment(
                updatedRoom.getEquipment()
        );

        room.setActive(
                updatedRoom.getActive() != null
                        ? updatedRoom.getActive()
                        : true
        );

        return roomRepository.save(
                room
        );
    }


    // =====================================================
    // ACTIVATE ROOM
    // =====================================================

    @Transactional
    public Room activateRoom(
            Long id) {

        Room room =
                getRoomById(id);

        room.setActive(true);

        return roomRepository.save(
                room
        );
    }


    // =====================================================
    // DEACTIVATE ROOM
    // =====================================================

    @Transactional
    public Room deactivateRoom(
            Long id) {

        Room room =
                getRoomById(id);

        room.setActive(false);

        return roomRepository.save(
                room
        );
    }


    // =====================================================
    // DELETE ROOM
    // =====================================================

    @Transactional
    public void deleteRoom(
            Long id) {

        Room room =
                getRoomById(id);

        roomRepository.delete(
                room
        );
    }


    // =====================================================
    // SEARCH BY TYPE
    // =====================================================

    public List<Room> getRoomsByType(
            String type) {

        if (isBlank(type)) {

            return getActiveRooms();
        }

        return roomRepository
                .findByTypeIgnoreCaseOrderByNameAsc(
                        type
                );
    }


    // =====================================================
    // SEARCH BY CAPACITY
    // =====================================================

    public List<Room> getRoomsByMinimumCapacity(
            Integer capacity) {

        if (capacity == null
                || capacity <= 0) {

            return getActiveRooms();
        }

        return roomRepository
                .findByCapacityGreaterThanEqualOrderByCapacityAsc(
                        capacity
                );
    }


    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}