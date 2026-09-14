package com.campus.campusbookingsystem.repository;

import com.campus.campusbookingsystem.entity.Notification;
import com.campus.campusbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Newest notifications first, for the bell dropdown
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Unread count for the bell badge
    long countByUserAndReadFalse(User user);

    // Unread notifications for "mark all read"
    List<Notification> findByUserAndReadFalse(User user);
}
