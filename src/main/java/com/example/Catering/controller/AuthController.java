package com.example.Catering.controller;

import com.example.Catering.dto.UserRegistrationDto;
import com.example.Catering.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // --- Регистрация ---
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid UserRegistrationDto userDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerClient(userDto);
            model.addAttribute("success", true);
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // --- Вход ---
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}