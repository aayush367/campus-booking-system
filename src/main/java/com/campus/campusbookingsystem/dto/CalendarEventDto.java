package com.campus.campusbookingsystem.dto;

import com.campus.campusbookingsystem.entity.Booking;

/**
 * Flat, JSON-friendly view of a booking for the calendar UI.
 * Kept as plain strings so Thymeleaf inline JSON serialization is safe
 * (no lazy JPA associations).
 */
public class CalendarEventDto {

    private Long id;
    private String room;
    private String date;       // yyyy-MM-dd
    private String startTime;  // HH:mm
    private String endTime;    // HH:mm
    private String status;
    private String requestedBy;
    private String eventType;
    private Integer attendees;

    public CalendarEventDto(Booking b) {
        this.id = b.getId();
        this.room = b.getRoom();
        this.date = b.getDate() != null ? b.getDate().toString() : null;
        this.startTime = b.getStartTime() != null ? b.getStartTime().toString() : null;
        this.endTime = b.getEndTime() != null ? b.getEndTime().toString() : null;
        this.status = b.getStatus();
        this.requestedBy = b.getUser() != null ? b.getUser().getFullName() : "";
        this.eventType = b.getEventType();
        this.attendees = b.getNumberOfAttendees();
    }

    public Long getId() { return id; }
    public String getRoom() { return room; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getRequestedBy() { return requestedBy; }
    public String getEventType() { return eventType; }
    public Integer getAttendees() { return attendees; }
}
