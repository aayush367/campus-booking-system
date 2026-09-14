package com.campus.campusbookingsystem.controller;

import com.campus.campusbookingsystem.entity.User;
import com.campus.campusbookingsystem.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(
                    name = "termsAccepted",
                    defaultValue = "false"
            ) boolean termsAccepted,
            Model model) {

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute(
                    "error",
                    "Password and confirmation password do not match."
            );

            user.setPassword("");
            return "register";
        }

        if (!termsAccepted) {
            model.addAttribute(
                    "error",
                    "You must accept the Terms and Conditions."
            );

            user.setPassword("");
            return "register";
        }

        try {
            userService.registerUser(user);

            return "redirect:/signin?registered=true";

        } catch (IllegalArgumentException exception) {

            model.addAttribute("error", exception.getMessage());
            user.setPassword("");

            return "register";

        } catch (Exception exception) {

            model.addAttribute(
                    "error",
                    "Registration could not be completed. Please try again."
            );

            user.setPassword("");
            return "register";
        }
    }
}