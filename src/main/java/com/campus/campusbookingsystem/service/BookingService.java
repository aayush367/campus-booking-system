package com.campus.campusbookingsystem.service;

import com.campus.campusbookingsystem.entity.Booking;
import com.campus.campusbookingsystem.entity.Room;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.repository.BookingRepository;
import com.campus.campusbookingsystem.repository.RoomRepository;
import com.campus.campusbookingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            NotificationService notificationService,
            AuditService auditService) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }


    // =====================================================
    // CREATE BOOKING
    // =====================================================

    @Transactional
    public Booking createBooking(
            Booking booking) {

        if (booking == null) {

            throw new IllegalArgumentException(
                    "Booking information is required."
            );
        }


        if (booking.getUser() == null) {

            throw new IllegalArgumentException(
                    "A logged-in user is required."
            );
        }


        if (isBlank(
                booking.getRoom()
        )) {

            throw new IllegalArgumentException(
                    "Room is required."
            );
        }


        if (isBlank(
                booking.getEventType()
        )) {

            throw new IllegalArgumentException(
                    "Event type is required."
            );
        }


        if (isBlank(
                booking.getDepartment()
        )) {

            throw new IllegalArgumentException(
                    "Department is required."
            );
        }


        if (isBlank(
                booking.getPriority()
        )) {

            throw new IllegalArgumentException(
                    "Priority level is required."
            );
        }


        if (booking.getNumberOfAttendees() == null
                || booking.getNumberOfAttendees() <= 0) {

            throw new IllegalArgumentException(
                    "Number of attendees must be greater than 0."
            );
        }


        if (booking.getDate() == null) {

            throw new IllegalArgumentException(
                    "Booking date is required."
            );
        }


        if (booking.getStartTime() == null) {

            throw new IllegalArgumentException(
                    "Start time is required."
            );
        }


        if (booking.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "End time is required."
            );
        }


        if (!booking
                .getEndTime()
                .isAfter(
                        booking.getStartTime()
                )) {

            throw new IllegalArgumentException(
                    "End time must be after the start time."
            );
        }


        if (booking.getSetupTime() == null) {

            booking.setSetupTime(
                    0
            );
        }


        if (booking.getCleanupTime() == null) {

            booking.setCleanupTime(
                    0
            );
        }


        if (booking.getRecurring() == null) {

            booking.setRecurring(
                    false
            );
        }


        // =================================================
        // ROOM CAPACITY CHECK
        // =================================================

        Room bookedRoom =
                roomRepository.findByNameIgnoreCase(booking.getRoom())
                        .orElse(null);

        if (bookedRoom != null
                && bookedRoom.getCapacity() != null
                && booking.getNumberOfAttendees() != null
                && booking.getNumberOfAttendees() > bookedRoom.getCapacity()) {

            throw new IllegalArgumentException(
                    "Number of attendees (" + booking.getNumberOfAttendees()
                            + ") exceeds the room capacity of "
                            + bookedRoom.getCapacity() + ".");
        }


        // =================================================
        // CONFLICT / DOUBLE-BOOKING CHECK
        // A confirmed (APPROVED) booking for the same room + date whose time
        // overlaps blocks anyone else — including another faculty member —
        // from taking that slot.
        // =================================================

        java.util.Optional<Booking> clash =
                findApprovedConflict(
                        booking.getRoom(),
                        booking.getDate(),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        null);

        if (clash.isPresent()) {

            Booking other = clash.get();
            String bookedBy = other.getUser() != null
                    ? other.getUser().getFullName() : "another user";

            throw new IllegalArgumentException(
                    booking.getRoom() + " is already booked on " + booking.getDate()
                            + " from " + other.getStartTime() + " to " + other.getEndTime()
                            + " by " + bookedBy
                            + ". Please choose a different time or room."
            );
        }


        // =================================================
        // STATUS BASED ON USER TYPE
        // =================================================

        User user =
                booking.getUser();

        String accountType =
                user.getAccountType();


        if (accountType != null
                && (
                accountType.equalsIgnoreCase(
                        "FACULTY"
                )
                        ||
                        accountType.equalsIgnoreCase(
                                "STAFF"
                        )
                        ||
                        accountType.equalsIgnoreCase(
                                "ADMIN"
                        )
        )) {

            /*
             Faculty / Staff / Admin bookings
             are automatically approved.
             */

            booking.setStatus(
                    "APPROVED"
            );

        } else {

            /*
             Student bookings require
             admin approval.
             */

            booking.setStatus(
                    "PENDING"
            );
        }


        booking.setCreatedAt(
                LocalDateTime.now()
        );


        Booking saved =
                bookingRepository.save(booking);


        auditService.record(
                "BOOKING_CREATED", "BOOKING", saved.getId(),
                saved.getUser().getFullName() + " created a booking for "
                        + saved.getRoom() + " on " + saved.getDate()
                        + " (" + saved.getStatus() + ").",
                saved.getUser().getFullName());


        // =================================================
        // NOTIFICATIONS
        // =================================================

        if ("APPROVED".equalsIgnoreCase(saved.getStatus())) {

            notificationService.notify(
                    saved.getUser(),
                    "Booking Approved",
                    "Your booking for " + saved.getRoom()
                            + " on " + saved.getDate()
                            + " was automatically approved.",
                    "APPROVED"
            );

        } else {

            notificationService.notify(
                    saved.getUser(),
                    "Booking Submitted",
                    "Your booking request for " + saved.getRoom()
                            + " on " + saved.getDate()
                            + " is pending admin approval.",
                    "PENDING"
            );

            // Let every admin know there is a request waiting
            for (User admin : userRepository.findByAccountTypeIgnoreCase("ADMIN")) {

                notificationService.notify(
                        admin,
                        "New Booking Request",
                        saved.getUser().getFullName()
                                + " requested " + saved.getRoom()
                                + " on " + saved.getDate() + ".",
                        "PENDING"
                );
            }
        }


        return saved;
    }


    // =====================================================
    // GET USER BOOKINGS
    // =====================================================

    public List<Booking> getBookingsForUser(
            User user) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }


        return bookingRepository
                .findByUserOrderByDateDescStartTimeDesc(
                        user
                );
    }


    // =====================================================
    // USER APPROVED BOOKINGS
    // Calendar for Student / Faculty
    // =====================================================

    public List<Booking> getApprovedBookingsForUser(
            User user) {

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required."
            );
        }


        return bookingRepository
                .findByUserAndStatusOrderByDateAscStartTimeAsc(
                        user,
                        "APPROVED"
                );
    }


    // =====================================================
    // GET ALL BOOKINGS
    // Admin
    // =====================================================

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    // =====================================================
    // GET ALL BOOKINGS BY STATUS
    // Admin
    // =====================================================

    public List<Booking> getBookingsByStatus(
            String status) {

        if (isBlank(status)) {

            throw new IllegalArgumentException(
                    "Booking status is required."
            );
        }


        return bookingRepository
                .findByStatusOrderByDateAscStartTimeAsc(
                        status.toUpperCase()
                );
    }


    // =====================================================
    // GET ONE BOOKING
    // =====================================================

    public Booking getBookingById(
            Long id) {

        if (id == null) {

            throw new IllegalArgumentException(
                    "Booking ID is required."
            );
        }


        return bookingRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Booking not found."
                                )
                );
    }


    // =====================================================
    // ADMIN APPROVE BOOKING
    // =====================================================

    @Transactional
    public Booking approveBooking(Long id) {
        return approveBooking(id, null, "Admin");
    }

    @Transactional
    public Booking approveBooking(
            Long id, String note, String actor) {

        Booking booking =
                getBookingById(id);


        if ("APPROVED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            return booking;
        }


        if ("CANCELLED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Cancelled bookings cannot be approved."
            );
        }


        java.util.Optional<Booking> approveClash =
                findApprovedConflict(
                        booking.getRoom(),
                        booking.getDate(),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        booking.getId());

        if (approveClash.isPresent()) {

            Booking other = approveClash.get();
            throw new IllegalArgumentException(
                    "Cannot approve: " + booking.getRoom() + " is already booked on "
                            + booking.getDate() + " from " + other.getStartTime()
                            + " to " + other.getEndTime() + "."
            );
        }


        booking.setStatus("APPROVED");

        if (note != null && !note.isBlank()) {
            booking.setAdminNote(note.trim());
        }


        Booking saved =
                bookingRepository.save(booking);

        notificationService.notify(
                saved.getUser(),
                "Booking Approved",
                "Your booking for " + saved.getRoom()
                        + " on " + saved.getDate() + " has been approved."
                        + (saved.getAdminNote() != null
                            ? " Note: " + saved.getAdminNote() : ""),
                "APPROVED"
        );

        auditService.record(
                "BOOKING_APPROVED", "BOOKING", saved.getId(),
                "Booking for " + saved.getRoom() + " on " + saved.getDate()
                        + " by " + saved.getUser().getFullName() + " was approved.",
                actor);

        return saved;
    }


    // =====================================================
    // ADMIN REJECT BOOKING
    // =====================================================

    @Transactional
    public Booking rejectBooking(Long id) {
        return rejectBooking(id, null, "Admin");
    }

    @Transactional
    public Booking rejectBooking(
            Long id, String note, String actor) {

        Booking booking =
                getBookingById(id);


        if ("REJECTED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            return booking;
        }


        if ("CANCELLED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Cancelled bookings cannot be rejected."
            );
        }


        booking.setStatus("REJECTED");

        if (note != null && !note.isBlank()) {
            booking.setAdminNote(note.trim());
        }


        Booking saved =
                bookingRepository.save(booking);

        notificationService.notify(
                saved.getUser(),
                "Booking Rejected",
                "Your booking for " + saved.getRoom()
                        + " on " + saved.getDate() + " was rejected."
                        + (saved.getAdminNote() != null
                            ? " Reason: " + saved.getAdminNote() : ""),
                "REJECTED"
        );

        auditService.record(
                "BOOKING_REJECTED", "BOOKING", saved.getId(),
                "Booking for " + saved.getRoom() + " on " + saved.getDate()
                        + " by " + saved.getUser().getFullName() + " was rejected."
                        + (saved.getAdminNote() != null
                            ? " Reason: " + saved.getAdminNote() : ""),
                actor);

        return saved;
    }


    // =====================================================
    // STUDENT / FACULTY CANCEL BOOKING
    // =====================================================

    @Transactional
    public Booking cancelBooking(
            Long bookingId,
            User loggedInUser) {

        if (loggedInUser == null) {

            throw new IllegalArgumentException(
                    "A logged-in user is required."
            );
        }


        Booking booking =
                getBookingById(
                        bookingId
                );


        if (booking.getUser() == null
                || booking
                .getUser()
                .getId() == null
                || loggedInUser.getId() == null
                || !booking
                .getUser()
                .getId()
                .equals(
                        loggedInUser.getId()
                )) {

            throw new IllegalArgumentException(
                    "You cannot cancel another user's booking."
            );
        }


        if ("CANCELLED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "This booking is already cancelled."
            );
        }


        if ("REJECTED".equalsIgnoreCase(
                booking.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Rejected bookings cannot be cancelled."
            );
        }


        boolean wasApproved =
                "APPROVED".equalsIgnoreCase(booking.getStatus());


        booking.setStatus(
                "CANCELLED"
        );


        Booking saved = bookingRepository.save(booking);

        auditService.record(
                "BOOKING_CANCELLED", "BOOKING", saved.getId(),
                loggedInUser.getFullName() + " cancelled their booking for "
                        + saved.getRoom() + " on " + saved.getDate() + ".",
                loggedInUser.getFullName());

        // If a confirmed booking is cancelled, let admins know the slot is free
        if (wasApproved) {
            for (User admin : userRepository.findByAccountTypeIgnoreCase("ADMIN")) {
                notificationService.notify(
                        admin,
                        "Booking Cancelled",
                        loggedInUser.getFullName() + " cancelled an approved booking for "
                                + saved.getRoom() + " on " + saved.getDate()
                                + ". The slot is now free.",
                        "INFO");
            }
        }

        return saved;
    }


    // =====================================================
    // USER BOOKING COUNTS
    // =====================================================

    public long getTotalBookings(
            User user) {

        if (user == null) {

            return 0;
        }


        return bookingRepository
                .countByUser(
                        user
                );
    }


    public long getPendingBookings(
            User user) {

        if (user == null) {

            return 0;
        }


        return bookingRepository
                .countByUserAndStatus(
                        user,
                        "PENDING"
                );
    }


    public long getApprovedBookings(
            User user) {

        if (user == null) {

            return 0;
        }


        return bookingRepository
                .countByUserAndStatus(
                        user,
                        "APPROVED"
                );
    }


    public long getRejectedBookings(
            User user) {

        if (user == null) {

            return 0;
        }


        return bookingRepository
                .countByUserAndStatus(
                        user,
                        "REJECTED"
                );
    }


    public long getCancelledBookings(
            User user) {

        if (user == null) {

            return 0;
        }


        return bookingRepository
                .countByUserAndStatus(
                        user,
                        "CANCELLED"
                );
    }


    // =====================================================
    // ADMIN GLOBAL COUNTS
    // =====================================================

    public long getTotalBookingCount() {

        return bookingRepository.count();
    }


    public long getBookingCountByStatus(
            String status) {

        if (isBlank(status)) {

            return 0;
        }


        return bookingRepository
                .countByStatus(
                        status.toUpperCase()
                );
    }


    // =====================================================
    // ANALYTICS AGGREGATIONS
    // =====================================================

    public java.util.Map<String, Long> countByDepartment() {
        return groupCount(bookingRepository.findAll(), Booking::getDepartment);
    }

    public java.util.Map<String, Long> countByEventType() {
        return groupCount(bookingRepository.findAll(), Booking::getEventType);
    }

    public java.util.Map<String, Long> countByRoom() {
        return groupCount(bookingRepository.findAll(), Booking::getRoom);
    }

    public java.util.Map<String, Long> countByMonth() {
        java.util.Map<String, Long> result = new java.util.TreeMap<>();
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
        for (Booking b : bookingRepository.findAll()) {
            if (b.getDate() == null) {
                continue;
            }
            String key = b.getDate().format(fmt);
            result.merge(key, 1L, Long::sum);
        }
        return result;
    }

    private java.util.Map<String, Long> groupCount(
            List<Booking> bookings,
            java.util.function.Function<Booking, String> keyFn) {

        java.util.Map<String, Long> result = new java.util.LinkedHashMap<>();
        for (Booking b : bookings) {
            String key = keyFn.apply(b);
            if (key == null || key.isBlank()) {
                key = "Unspecified";
            }
            result.merge(key, 1L, Long::sum);
        }
        return result;
    }


    // =====================================================
    // CONFLICT DETECTION
    // =====================================================

    /**
     * Finds an already-APPROVED booking for the SAME room and date whose time
     * range overlaps [start, end). Room names are matched case-insensitively
     * and trimmed so minor formatting differences can't slip a clash through.
     * ignoreBookingId lets a booking skip comparing against itself.
     */
    public java.util.Optional<Booking> findApprovedConflict(
            String room,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            Long ignoreBookingId) {

        if (isBlank(room) || date == null
                || start == null || end == null) {

            return java.util.Optional.empty();
        }

        String wanted = room.trim();

        for (Booking existing : bookingRepository.findByDateAndStatus(date, "APPROVED")) {

            if (ignoreBookingId != null
                    && ignoreBookingId.equals(existing.getId())) {
                continue;
            }

            if (existing.getRoom() == null
                    || !existing.getRoom().trim().equalsIgnoreCase(wanted)) {
                continue;
            }

            if (existing.getStartTime() == null
                    || existing.getEndTime() == null) {
                continue;
            }

            boolean overlaps =
                    start.isBefore(existing.getEndTime())
                            && end.isAfter(existing.getStartTime());

            if (overlaps) {
                return java.util.Optional.of(existing);
            }
        }

        return java.util.Optional.empty();
    }

    public boolean hasApprovedConflict(
            String room,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            Long ignoreBookingId) {

        return findApprovedConflict(room, date, start, end, ignoreBookingId)
                .isPresent();
    }


    // =====================================================
    // VALIDATION HELPER
    // =====================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}