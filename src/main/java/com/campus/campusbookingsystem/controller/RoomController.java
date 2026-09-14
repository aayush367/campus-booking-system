package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.Room;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.AuditService;
import com.campus.campusbookingsystem.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoomController {

    private final RoomService roomService;
    private final AuditService auditService;

    public RoomController(RoomService roomService, AuditService auditService) {
        this.roomService = roomService;
        this.auditService = auditService;
    }

    private String actor(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null ? user.getFullName() : "System";
    }

    // =====================================================
    // LIST + FORM
    // =====================================================

    @GetMapping("/admin/rooms")
    public String listRooms(HttpSession session, Model model) {

        if (notAdmin(session)) {
            return "redirect:/signin";
        }

        model.addAttribute("user", session.getAttribute("loggedInUser"));
        model.addAttribute("rooms", roomService.getAllRooms());

        if (!model.containsAttribute("room")) {
            model.addAttribute("room", new Room());
        }

        return "admin-rooms";
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping("/admin/rooms")
    public String createRoom(
            @ModelAttribute("room") Room room,
            HttpSession session,
            Model model) {

        if (notAdmin(session)) {
            return "redirect:/signin";
        }

        try {
            Room created = roomService.createRoom(room);
            auditService.record("ROOM_CREATED", "ROOM", created.getId(),
                    "Room '" + created.getName() + "' (" + created.getType()
                            + ", capacity " + created.getCapacity() + ") was created.",
                    actor(session));
            return "redirect:/admin/rooms?created=true";

        } catch (IllegalArgumentException exception) {
            model.addAttribute("user", session.getAttribute("loggedInUser"));
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("error", exception.getMessage());
            return "admin-rooms";
        }
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PostMapping("/admin/rooms/{id}/update")
    public String updateRoom(
            @PathVariable Long id,
            @ModelAttribute("room") Room room,
            HttpSession session,
            Model model) {

        if (notAdmin(session)) {
            return "redirect:/signin";
        }

        try {
            Room updated = roomService.updateRoom(id, room);
            auditService.record("ROOM_UPDATED", "ROOM", updated.getId(),
                    "Room '" + updated.getName() + "' was updated.", actor(session));
            return "redirect:/admin/rooms?updated=true";

        } catch (IllegalArgumentException exception) {
            model.addAttribute("user", session.getAttribute("loggedInUser"));
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("error", exception.getMessage());
            return "admin-rooms";
        }
    }

    // =====================================================
    // ACTIVATE / DEACTIVATE / DELETE
    // =====================================================

    @PostMapping("/admin/rooms/{id}/status")
    public String toggleStatus(
            @PathVariable Long id,
            @RequestParam("active") boolean active,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/signin";
        }

        Room room = active
                ? roomService.activateRoom(id)
                : roomService.deactivateRoom(id);

        auditService.record("ROOM_STATUS_CHANGED", "ROOM", room.getId(),
                "Room '" + room.getName() + "' was "
                        + (active ? "activated." : "deactivated."),
                actor(session));

        return "redirect:/admin/rooms";
    }

    @PostMapping("/admin/rooms/{id}/delete")
    public String deleteRoom(
            @PathVariable Long id,
            HttpSession session) {

        if (notAdmin(session)) {
            return "redirect:/signin";
        }

        Room room = roomService.getRoomById(id);
        String name = room.getName();
        roomService.deleteRoom(id);
        auditService.record("ROOM_DELETED", "ROOM", id,
                "Room '" + name + "' was deleted.", actor(session));
        return "redirect:/admin/rooms?deleted=true";
    }

    // =====================================================
    // HELPER
    // =====================================================

    private boolean notAdmin(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        return user == null
                || user.getAccountType() == null
                || !user.getAccountType().equalsIgnoreCase("ADMIN");
    }
}
