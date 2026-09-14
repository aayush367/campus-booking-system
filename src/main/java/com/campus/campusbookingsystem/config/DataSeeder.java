package com.campus.campusbookingsystem.config;

import com.campus.campusbookingsystem.entity.Room;
import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.repository.RoomRepository;
import com.campus.campusbookingsystem.repository.UserRepository;
import com.campus.campusbookingsystem.service.RoomService;
import com.campus.campusbookingsystem.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds baseline data on startup. Fully idempotent:
 * it only creates rows that are missing and never touches existing data.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final UserRepository userRepository;
    private final UserService userService;

    public DataSeeder(
            RoomRepository roomRepository,
            RoomService roomService,
            UserRepository userRepository,
            UserService userService) {

        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {

        seedRooms();
        seedAdmin();
        seedDemoUsers();
    }

    // =====================================================
    // ROOMS (from the Figma prototype)
    // =====================================================

    private void seedRooms() {

        createRoomIfMissing(
                "Engineering Hall A101", "Lecture Hall", 100, "Engineering Building",
                "Projector, Whiteboard, Audio System, Podium, WiFi");

        createRoomIfMissing(
                "Computer Lab B205", "Lab", 40, "Science Building",
                "Computers, Projector, Whiteboard, WiFi, Air Conditioning");

        createRoomIfMissing(
                "Main Auditorium", "Auditorium", 500, "Main Building",
                "Stage, Lighting System, Professional Audio, Projector, Microphones");

        createRoomIfMissing(
                "Meeting Room C303", "Meeting Room", 15, "Business Building",
                "Conference Table, Video Conferencing, Whiteboard");

        createRoomIfMissing(
                "Science Lab D102", "Lab", 30, "Science Building",
                "Lab Equipment, Safety Gear, Workbenches, Ventilation");
    }

    private void createRoomIfMissing(
            String name, String type, int capacity,
            String building, String equipment) {

        if (roomRepository.existsByNameIgnoreCase(name)) {
            return;
        }

        Room room = new Room();
        room.setName(name);
        room.setType(type);
        room.setCapacity(capacity);
        room.setBuilding(building);
        room.setEquipment(equipment);
        room.setActive(true);

        roomService.createRoom(room);

        System.out.println("[DataSeeder] Created room: " + name);
    }

    // =====================================================
    // DEFAULT ADMIN
    // =====================================================

    private void seedAdmin() {

        // Always ensure the documented default admin exists so the
        // sign-in Quick Login works. Only created if this exact email is missing.
        createUserIfMissing("System Administrator", "admin@campus.edu", "Admin@1234",
                "ADMIN", "Administration", "ADMIN-001");
    }

    // =====================================================
    // DEMO STUDENT + FACULTY (for the sign-in Quick Login)
    // =====================================================

    private void seedDemoUsers() {

        createUserIfMissing("Demo Student", "student@campus.edu", "Student@123",
                "STUDENT", "Computer Science", "STU-0001");

        createUserIfMissing("Demo Faculty", "faculty@campus.edu", "Faculty@123",
                "FACULTY", "Physics", "FAC-0001");
    }

    private void createUserIfMissing(String fullName, String email, String password,
                                     String accountType, String department, String idNumber) {

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPassword(password);
        u.setPhone("");
        u.setAccountType(accountType);
        u.setDepartment(department);
        u.setStudentId(idNumber);

        userService.registerUser(u);

        System.out.println("[DataSeeder] Created demo user -> " + email + " / " + password);
    }
}
