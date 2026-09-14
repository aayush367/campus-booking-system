package com.campus.campusbookingsystem.service;

import com.campus.campusbookingsystem.entity.Notification;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // =====================================================
    // CREATE NOTIFICATION
    // =====================================================

    @Transactional
    public Notification notify(
            User user,
            String title,
            String message,
            String type) {

        if (user == null) {
            return null;
        }

        Notification notification =
                new Notification(user, title, message, type);

        return notificationRepository.save(notification);
    }

    // =====================================================
    // READ
    // =====================================================

    public List<Notification> getNotificationsForUser(User user) {

        if (user == null) {
            return List.of();
        }

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {

        if (user == null) {
            return 0;
        }

        return notificationRepository.countByUserAndReadFalse(user);
    }

    // =====================================================
    // MARK ALL READ
    // =====================================================

    @Transactional
    public void markAllRead(User user) {

        if (user == null) {
            return;
        }

        List<Notification> unread =
                notificationRepository.findByUserAndReadFalse(user);

        for (Notification notification : unread) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unread);
    }
}
