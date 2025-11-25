package com.example.Catering.controller;

import com.example.Catering.entity.User;
import com.example.Catering.repository.UserRepository;
import com.example.Catering.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    public ProfileController(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        // Если пользователь не авторизован — перенаправляем на вход
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?error";
        }

        // Получаем email из Spring Security
        String email = authentication.getName();

        // Находим пользователя в БД
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Получаем заказы пользователя
        var orders = orderService.getOrdersByUser(user);

        // Передаём данные в шаблон
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "profile";
    }
}