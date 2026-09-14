package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notificationsPage(HttpSession session, Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("notifications",
                notificationService.getNotificationsForUser(loggedInUser));
        model.addAttribute("unreadCount",
                notificationService.getUnreadCount(loggedInUser));

        return "notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(
            HttpSession session,
            HttpServletRequest request) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        notificationService.markAllRead(loggedInUser);

        String referer = request.getHeader("Referer");

        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/dashboard";
    }
}
