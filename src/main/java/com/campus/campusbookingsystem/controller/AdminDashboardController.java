package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.dto.CalendarEventDto;
import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.AuditService;
import com.campus.campusbookingsystem.service.BookingService;
import com.campus.campusbookingsystem.service.NotificationService;
import com.campus.campusbookingsystem.service.RoomService;
import com.campus.campusbookingsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminDashboardController {

    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public AdminDashboardController(
            BookingService bookingService,
            UserService userService,
            RoomService roomService,
            NotificationService notificationService,
            AuditService auditService) {

        this.bookingService = bookingService;
        this.userService = userService;
        this.roomService = roomService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        // Must be logged in
        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        // Must be ADMIN
        if (loggedInUser.getAccountType() == null
                || !loggedInUser
                .getAccountType()
                .equalsIgnoreCase("ADMIN")) {

            return "redirect:/dashboard";
        }


        // ========================================
        // ADMIN USER
        // ========================================

        model.addAttribute(
                "user",
                loggedInUser
        );


        // ========================================
        // ALL BOOKINGS
        // ========================================

        List<Booking> allBookings =
                bookingService.getAllBookings();

        model.addAttribute(
                "allBookings",
                allBookings
        );


        // ========================================
        // PENDING BOOKINGS
        // ========================================

        List<Booking> pendingBookingsList =
                bookingService.getBookingsByStatus(
                        "PENDING"
                );

        model.addAttribute(
                "pendingBookingsList",
                pendingBookingsList
        );


        // ========================================
        // APPROVED BOOKINGS FOR CALENDAR
        // ========================================

        List<Booking> calendarBookings =
                bookingService.getBookingsByStatus(
                        "APPROVED"
                );

        model.addAttribute(
                "calendarBookings",
                calendarBookings
        );

        model.addAttribute(
                "calendarEvents",
                calendarBookings.stream().map(CalendarEventDto::new).toList()
        );


        // ========================================
        // GLOBAL BOOKING STATISTICS
        // ========================================

        model.addAttribute(
                "totalBookings",
                bookingService.getTotalBookingCount()
        );

        model.addAttribute(
                "pendingBookings",
                bookingService.getBookingCountByStatus(
                        "PENDING"
                )
        );

        model.addAttribute(
                "approvedBookings",
                bookingService.getBookingCountByStatus(
                        "APPROVED"
                )
        );

        model.addAttribute(
                "rejectedBookings",
                bookingService.getBookingCountByStatus(
                        "REJECTED"
                )
        );


        // ========================================
        // USERS
        // ========================================

        List<User> users =
                userService.getAllUsers();

        model.addAttribute(
                "allUsers",
                users
        );

        model.addAttribute(
                "totalUsers",
                users.size()
        );


        // ========================================
        // ANALYTICS
        // ========================================

        long total =
                bookingService.getTotalBookingCount();

        long approved =
                bookingService.getBookingCountByStatus(
                        "APPROVED"
                );


        double approvalRate = 0.0;

        if (total > 0) {

            approvalRate =
                    ((double) approved / total) * 100;
        }


        model.addAttribute(
                "approvalRate",
                String.format(
                        "%.1f",
                        approvalRate
                )
        );


        int totalAttendees =
                allBookings
                        .stream()
                        .filter(
                                booking ->
                                        booking
                                                .getNumberOfAttendees()
                                                != null
                        )
                        .mapToInt(
                                Booking::getNumberOfAttendees
                        )
                        .sum();


        model.addAttribute(
                "totalAttendees",
                totalAttendees
        );


        double averageAttendees = 0.0;

        if (!allBookings.isEmpty()) {

            averageAttendees =
                    (double) totalAttendees
                            / allBookings.size();
        }


        model.addAttribute(
                "averageAttendees",
                String.format(
                        "%.0f",
                        averageAttendees
                )
        );


        // ========================================
        // ADMIN NEW BOOKING FORM
        // ========================================

        Booking newBooking =
                new Booking();

        newBooking.setDepartment(
                loggedInUser.getDepartment()
        );

        model.addAttribute(
                "booking",
                newBooking
        );


        // ========================================
        // ROOMS + NOTIFICATIONS
        // ========================================

        model.addAttribute(
                "rooms",
                roomService.getActiveRooms()
        );

        model.addAttribute(
                "allRooms",
                roomService.getAllRooms()
        );

        model.addAttribute(
                "notifications",
                notificationService.getNotificationsForUser(loggedInUser)
        );

        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(loggedInUser)
        );


        // ========================================
        // ANALYTICS BREAKDOWNS + AUDIT TRAIL
        // ========================================

        model.addAttribute("byDepartment", bookingService.countByDepartment());
        model.addAttribute("byEventType", bookingService.countByEventType());
        model.addAttribute("byRoom", bookingService.countByRoom());
        model.addAttribute("byMonth", bookingService.countByMonth());

        model.addAttribute("auditLogs", auditService.getRecent(100));


        return "admin-dashboard";
    }
}