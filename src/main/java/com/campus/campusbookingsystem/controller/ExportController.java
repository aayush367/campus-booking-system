package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.AuditLog;
import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.AuditService;
import com.campus.campusbookingsystem.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExportController {

    private final BookingService bookingService;
    private final AuditService auditService;

    public ExportController(BookingService bookingService, AuditService auditService) {
        this.bookingService = bookingService;
        this.auditService = auditService;
    }

    @GetMapping("/admin/bookings/export.csv")
    public ResponseEntity<String> exportBookings(HttpSession session) {

        if (notAdmin(session)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Room,Requested By,Department,Event Type,Priority,Date,")
           .append("Start,End,Attendees,Status,Admin Note\n");

        for (Booking b : bookingService.getAllBookings()) {
            csv.append(b.getId()).append(',')
               .append(q(b.getRoom())).append(',')
               .append(q(b.getUser() != null ? b.getUser().getFullName() : "")).append(',')
               .append(q(b.getDepartment())).append(',')
               .append(q(b.getEventType())).append(',')
               .append(q(b.getPriority())).append(',')
               .append(b.getDate()).append(',')
               .append(b.getStartTime()).append(',')
               .append(b.getEndTime()).append(',')
               .append(b.getNumberOfAttendees()).append(',')
               .append(q(b.getStatus())).append(',')
               .append(q(b.getAdminNote())).append('\n');
        }

        return csvResponse(csv.toString(), "bookings.csv");
    }

    @GetMapping("/admin/audit/export.csv")
    public ResponseEntity<String> exportAudit(HttpSession session) {

        if (notAdmin(session)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        StringBuilder csv = new StringBuilder();
        csv.append("Timestamp,Action,Entity,Entity ID,Performed By,Description\n");

        for (AuditLog log : auditService.getAll()) {
            csv.append(log.getCreatedAt()).append(',')
               .append(q(log.getAction())).append(',')
               .append(q(log.getEntityType())).append(',')
               .append(log.getEntityId() != null ? log.getEntityId() : "").append(',')
               .append(q(log.getPerformedBy())).append(',')
               .append(q(log.getDescription())).append('\n');
        }

        return csvResponse(csv.toString(), "audit-trail.csv");
    }

    // -------------------------------------------------

    private ResponseEntity<String> csvResponse(String body, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    private String q(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private boolean notAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user == null || user.getAccountType() == null
                || !user.getAccountType().equalsIgnoreCase("ADMIN");
    }
}
