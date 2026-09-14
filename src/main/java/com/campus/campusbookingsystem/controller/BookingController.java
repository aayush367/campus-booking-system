package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.BookingService;
import com.campus.campusbookingsystem.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(
            BookingService bookingService,
            RoomService roomService) {

        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping("/booking/new")
    public String showBookingForm(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        Booking booking = new Booking();

        booking.setDepartment(
                loggedInUser.getDepartment()
        );

        model.addAttribute("booking", booking);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("rooms", roomService.getActiveRooms());

        return "new-booking";
    }

    @PostMapping("/booking")
    public String createBooking(
            @ModelAttribute("booking") Booking booking,
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        try {

            booking.setUser(loggedInUser);

            booking.setStatus("PENDING");

            bookingService.createBooking(booking);

            return redirectAfterBooking(loggedInUser, true);

        } catch (IllegalArgumentException exception) {

            model.addAttribute("error", exception.getMessage());
            model.addAttribute("user", loggedInUser);
            model.addAttribute("rooms", roomService.getActiveRooms());

            return "new-booking";

        } catch (Exception exception) {

            model.addAttribute(
                    "error",
                    "Booking could not be submitted. Please try again."
            );
            model.addAttribute("user", loggedInUser);
            model.addAttribute("rooms", roomService.getActiveRooms());

            return "new-booking";
        }
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(
            @PathVariable("id") Long bookingId,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        try {

            bookingService.cancelBooking(
                    bookingId,
                    loggedInUser
            );

            return redirectAfterBooking(loggedInUser, false)
                    + "&bookingCancelled=true";

        } catch (IllegalArgumentException exception) {

            return redirectAfterBooking(loggedInUser, false)
                    + "&cancelError=true";
        }
    }

    // Route the user back to their own dashboard after a booking action
    private String redirectAfterBooking(User user, boolean success) {

        String accountType = user.getAccountType() == null
                ? ""
                : user.getAccountType().trim().toUpperCase();

        String base;

        if (accountType.equals("FACULTY") || accountType.equals("STAFF")) {
            base = "redirect:/faculty/dashboard";
        } else if (accountType.equals("ADMIN")) {
            base = "redirect:/admin/dashboard";
        } else {
            base = "redirect:/dashboard";
        }

        return success ? base + "?bookingSuccess=true" : base + "?ts=" + System.currentTimeMillis();
    }
}
