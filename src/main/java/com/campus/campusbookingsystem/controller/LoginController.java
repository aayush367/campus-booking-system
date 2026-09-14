package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.AuditService;
import com.campus.campusbookingsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userService;
    private final AuditService auditService;

    public LoginController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping("/signin")
    public String showSignInPage() {
        return "signin";
    }

    @PostMapping("/signin")
    public String signIn(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        User user = userService.authenticateUser(
                email,
                password
        );

        if (user == null) {

            model.addAttribute(
                    "error",
                    "Invalid email or password."
            );

            return "signin";
        }

        auditService.record("USER_LOGIN", "USER", user.getId(),
                user.getFullName() + " (" + user.getAccountType()
                        + ") signed in.", user.getFullName());

        // Save logged in user
        session.setAttribute(
                "loggedInUser",
                user
        );

        session.setAttribute(
                "userId",
                user.getId()
        );

        session.setAttribute(
                "userName",
                user.getFullName()
        );

        session.setAttribute(
                "accountType",
                user.getAccountType()
        );


        // ========================================
        // ROLE BASED REDIRECT
        // ========================================

        String accountType =
                user.getAccountType();

        if (accountType == null) {

            return "redirect:/dashboard";
        }

        accountType =
                accountType
                        .trim()
                        .toUpperCase();


        System.out.println(
                "LOGIN ACCOUNT TYPE = "
                        + accountType
        );


        // ADMIN
        if (accountType.equals("ADMIN")) {

            return "redirect:/admin/dashboard";
        }


        // FACULTY / STAFF
        if (accountType.equals("FACULTY")
                || accountType.equals("STAFF")) {

            return "redirect:/faculty/dashboard";
        }


        // STUDENT
        return "redirect:/dashboard";
    }


    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/signin?logout=true";
    }
}