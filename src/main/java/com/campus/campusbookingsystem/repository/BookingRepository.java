package com.campus.campusbookingsystem.repository;

import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get all bookings for a specific user
    // Newest bookings first
    List<Booking> findByUserOrderByDateDescStartTimeDesc(
            User user
    );

    // Get bookings for a specific user by status
    // Used for faculty/student calendar
    List<Booking> findByUserAndStatusOrderByDateAscStartTimeAsc(
            User user,
            String status
    );

    // Get all bookings in the system by status
    // Used by admin dashboard
    List<Booking> findByStatusOrderByDateAscStartTimeAsc(
            String status
    );

    // All bookings for a room on a given date with a given status
    // Used for double-booking / conflict detection
    List<Booking> findByRoomAndDateAndStatus(
            String room,
            LocalDate date,
            String status
    );

    // All bookings on a given date with a given status.
    // Used for conflict detection with case-insensitive / trimmed room matching.
    List<Booking> findByDateAndStatus(
            LocalDate date,
            String status
    );

    // Count all bookings for a user
    long countByUser(
            User user
    );

    // Count bookings for a user by status
    long countByUserAndStatus(
            User user,
            String status
    );

    // Count all bookings in system by status
    long countByStatus(
            String status
    );
}
