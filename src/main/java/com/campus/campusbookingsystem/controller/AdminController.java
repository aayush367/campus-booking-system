package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String showUsers(HttpSession session, Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/signin";
        }

        if (loggedInUser.getAccountType() == null
                || !loggedInUser.getAccountType().equalsIgnoreCase("ADMIN")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin-users";
    }
}
