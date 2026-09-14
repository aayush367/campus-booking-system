package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.dto.CalendarEventDto;
import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.BookingService;
import com.campus.campusbookingsystem.service.NotificationService;
import com.campus.campusbookingsystem.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final NotificationService notificationService;

    public DashboardController(
            BookingService bookingService,
            RoomService roomService,
            NotificationService notificationService) {

        this.bookingService = bookingService;
        this.roomService = roomService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        model.addAttribute("user", loggedInUser);

        model.addAttribute(
                "bookings",
                bookingService.getBookingsForUser(loggedInUser)
        );

        model.addAttribute(
                "totalBookings",
                bookingService.getTotalBookings(loggedInUser)
        );

        model.addAttribute(
                "pendingBookings",
                bookingService.getPendingBookings(loggedInUser)
        );

        model.addAttribute(
                "approvedBookings",
                bookingService.getApprovedBookings(loggedInUser)
        );

        model.addAttribute(
                "rejectedBookings",
                bookingService.getRejectedBookings(loggedInUser)
        );

        // Active rooms for the New Booking form
        model.addAttribute("rooms", roomService.getActiveRooms());

        // Notifications for the bell dropdown
        model.addAttribute(
                "notifications",
                notificationService.getNotificationsForUser(loggedInUser)
        );
        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(loggedInUser)
        );

        // Approved bookings for the calendar view (JSON-friendly DTOs)
        List<CalendarEventDto> calendarEvents =
                bookingService.getApprovedBookingsForUser(loggedInUser).stream()
                        .map(CalendarEventDto::new)
                        .toList();
        model.addAttribute("calendarEvents", calendarEvents);

        // Used by the New Booking modal
        Booking newBooking = new Booking();
        newBooking.setDepartment(loggedInUser.getDepartment());

        model.addAttribute("booking", newBooking);

        return "dashboard";
    }
}
