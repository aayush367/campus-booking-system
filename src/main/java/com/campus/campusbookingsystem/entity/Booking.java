package com.campus.campusbookingsystem.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who created the booking
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // For now we store the room as text.
    // Later we can connect this to a Room entity.
    @Column(nullable = false)
    private String room;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String priority;

    @Column(name = "number_of_attendees", nullable = false)
    private Integer numberOfAttendees;

    @Column(name = "booking_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "setup_time")
    private Integer setupTime;

    @Column(name = "cleanup_time")
    private Integer cleanupTime;

    @Column(name = "equipment_needed", length = 500)
    private String equipmentNeeded;

    @Column(length = 2000)
    private String description;

    private Boolean recurring = false;

    @Column(nullable = false)
    private String status = "PENDING";

    // Optional note left by an admin when approving or rejecting
    @Column(name = "admin_note", length = 500)
    private String adminNote;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Booking() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Integer setupTime) {
        this.setupTime = setupTime;
    }

    public Integer getCleanupTime() {
        return cleanupTime;
    }

    public void setCleanupTime(Integer cleanupTime) {
        this.cleanupTime = cleanupTime;
    }

    public String getEquipmentNeeded() {
        return equipmentNeeded;
    }

    public void setEquipmentNeeded(String equipmentNeeded) {
        this.equipmentNeeded = equipmentNeeded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}