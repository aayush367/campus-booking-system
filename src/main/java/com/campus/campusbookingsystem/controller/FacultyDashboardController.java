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
public class FacultyDashboardController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final NotificationService notificationService;

    public FacultyDashboardController(
            BookingService bookingService,
            RoomService roomService,
            NotificationService notificationService) {

        this.bookingService = bookingService;
        this.roomService = roomService;
        this.notificationService = notificationService;
    }

    @GetMapping("/faculty/dashboard")
    public String facultyDashboard(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute(
                        "loggedInUser"
                );

        // User must be logged in
        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        String accountType =
                loggedInUser.getAccountType();

        // Only Faculty or Staff can access this dashboard
        if (accountType == null
                || (!accountType.equalsIgnoreCase("FACULTY")
                && !accountType.equalsIgnoreCase("STAFF"))) {

            return "redirect:/dashboard";
        }


        // =====================================
        // Logged in faculty user
        // =====================================

        model.addAttribute(
                "user",
                loggedInUser
        );


        // =====================================
        // All bookings belonging to faculty
        // =====================================

        model.addAttribute(
                "bookings",
                bookingService.getBookingsForUser(
                        loggedInUser
                )
        );


        // =====================================
        // Approved bookings for calendar
        // =====================================

        model.addAttribute(
                "calendarBookings",
                bookingService.getApprovedBookingsForUser(
                        loggedInUser
                )
        );


        // =====================================
        // Dashboard statistics
        // =====================================

        model.addAttribute(
                "totalBookings",
                bookingService.getTotalBookings(
                        loggedInUser
                )
        );

        model.addAttribute(
                "pendingBookings",
                bookingService.getPendingBookings(
                        loggedInUser
                )
        );

        model.addAttribute(
                "approvedBookings",
                bookingService.getApprovedBookings(
                        loggedInUser
                )
        );

        model.addAttribute(
                "rejectedBookings",
                bookingService.getRejectedBookings(
                        loggedInUser
                )
        );


        // =====================================
        // Rooms + notifications
        // =====================================

        model.addAttribute(
                "rooms",
                roomService.getActiveRooms()
        );

        List<CalendarEventDto> calendarEvents =
                bookingService.getApprovedBookingsForUser(loggedInUser).stream()
                        .map(CalendarEventDto::new)
                        .toList();
        model.addAttribute("calendarEvents", calendarEvents);

        model.addAttribute(
                "notifications",
                notificationService.getNotificationsForUser(loggedInUser)
        );

        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(loggedInUser)
        );


        // =====================================
        // New booking form
        // =====================================

        Booking booking =
                new Booking();

        booking.setDepartment(
                loggedInUser.getDepartment()
        );

        model.addAttribute(
                "booking",
                booking
        );


        return "faculty-dashboard";
    }
}