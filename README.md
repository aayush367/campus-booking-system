# Campus Event & Facility Booking Management System

ICT302 – Information Technology Project 2 (KOI University, internal capstone project). A role-based web application for booking campus facilities (lecture theatres, seminar rooms, labs) that replaces manual, email-based room requests.

## Tech Stack

- Spring Boot 4.1.0 (Java 17)
- Thymeleaf (server-rendered templates)
- Spring Data JPA / Hibernate
- MySQL
- Spring Security Crypto (BCrypt password hashing)
- Chart.js (analytics dashboard)

## Features

- Role-based accounts: Student, Faculty/Staff, Administrator
- Room search and booking submission with automatic conflict detection
- Auto-approval for Faculty/Staff/Admin bookings; approval queue for Student bookings
- In-app notifications on booking status changes
- Weekly time-grid calendar per role
- Administrator room management (CRUD), approval workflow with reasons
- Searchable, CSV-exportable audit trail
- Analytics dashboard (bookings by status, event type, department, month, room)

## Getting Started

1. Create a MySQL database named `campus_booking_system` and a user with access to it.
2. Copy the config template and fill in your own database credentials:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   Edit `spring.datasource.username` / `spring.datasource.password` in that file to match your MySQL setup. This file is git-ignored and never committed.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Visit `http://localhost:8080`.

On first run, `DataSeeder` seeds a few demo rooms and three demo accounts (if no admin account exists yet):

| Role | Email | Password |
|---|---|---|
| Admin | admin@campus.edu | Admin@1234 |
| Faculty/Staff | faculty@campus.edu | Faculty@123 |
| Student | student@campus.edu | Student@123 |

See the Sign In page's "Quick Login" buttons to log in with these instantly.

## Project Team

| Student ID | Name | Area of Ownership |
|---|---|---|
| 20034795 | Aayush Upreti | Notifications & Student UI |
| 20031135 | Chetan Bhatta | Rooms & Booking Forms |
| 20032773 | Bhaskar Adhikari | Booking Rules & Approval Workflow |
| 20031191 | Dipu Bimali | Calendar, Analytics & Reporting |
| 20030704 | Unish Mainali | Audit Trail, Design System & QA |
