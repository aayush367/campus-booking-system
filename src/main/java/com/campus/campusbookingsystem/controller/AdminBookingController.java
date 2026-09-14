package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/admin/bookings")
    public String showBookings(HttpSession session, Model model) {

        User loggedInUser = requireAdmin(session);
        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("user", loggedInUser);
        return "admin-bookings";
    }

    @PostMapping("/admin/bookings/{id}/approve")
    public String approveBooking(
            @PathVariable Long id,
            @RequestParam(value = "note", required = false) String note,
            HttpSession session,
            HttpServletRequest request) {

        User admin = requireAdmin(session);
        if (admin == null) {
            return "redirect:/signin";
        }

        try {
            bookingService.approveBooking(id, note, admin.getFullName());
            return back(request, "approved=true");
        } catch (IllegalArgumentException exception) {
            return back(request, "error");
        }
    }

    @PostMapping("/admin/bookings/{id}/reject")
    public String rejectBooking(
            @PathVariable Long id,
            @RequestParam(value = "note", required = false) String note,
            HttpSession session,
            HttpServletRequest request) {

        User admin = requireAdmin(session);
        if (admin == null) {
            return "redirect:/signin";
        }

        try {
            bookingService.rejectBooking(id, note, admin.getFullName());
            return back(request, "rejected=true");
        } catch (IllegalArgumentException exception) {
            return back(request, "error");
        }
    }

    // -------------------------------------------------

    private User requireAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getAccountType() == null
                || !user.getAccountType().equalsIgnoreCase("ADMIN")) {
            return null;
        }
        return user;
    }

    // Redirect back to whichever admin page triggered the action
    private String back(HttpServletRequest request, String flag) {
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/admin/dashboard")) {
            return "redirect:/admin/dashboard?" + flag;
        }
        return "redirect:/admin/bookings?" + flag;
    }
}
