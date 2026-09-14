package com.campus.campusbookingsystem.service;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public User registerUser(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User information is required."
            );
        }

        if (isBlank(user.getFullName())) {
            throw new IllegalArgumentException(
                    "Full name is required."
            );
        }

        if (isBlank(user.getEmail())) {
            throw new IllegalArgumentException(
                    "Email address is required."
            );
        }

        if (isBlank(user.getPassword())
                || user.getPassword().length() < 8) {

            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        if (isBlank(user.getAccountType())) {
            throw new IllegalArgumentException(
                    "Account type is required."
            );
        }

        if (isBlank(user.getDepartment())) {
            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        if (isBlank(user.getStudentId())) {
            throw new IllegalArgumentException(
                    "Student or staff ID is required."
            );
        }

        String cleanedEmail =
                user.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(cleanedEmail)) {
            throw new IllegalArgumentException(
                    "An account already exists with this email address."
            );
        }

        user.setFullName(
                user.getFullName().trim()
        );

        user.setEmail(cleanedEmail);

        user.setAccountType(
                user.getAccountType().trim().toUpperCase()
        );

        user.setDepartment(
                user.getDepartment().trim()
        );

        user.setStudentId(
                user.getStudentId().trim()
        );

        if (user.getPhone() != null) {
            user.setPhone(
                    user.getPhone().trim()
            );
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        User saved = userRepository.save(user);

        auditService.record("USER_REGISTERED", "USER", saved.getId(),
                saved.getFullName() + " registered a new " + saved.getAccountType()
                        + " account.",
                saved.getFullName());

        return saved;
    }

    public User authenticateUser(
            String email,
            String rawPassword) {

        if (isBlank(email) || isBlank(rawPassword)) {
            return null;
        }

        String cleanedEmail =
                email.trim().toLowerCase();

        User user = userRepository
                .findByEmailIgnoreCase(cleanedEmail)
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                rawPassword,
                user.getPassword())) {

            return null;
        }

        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}