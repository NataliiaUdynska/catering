package com.example.Catering.controller;

import com.example.Catering.dto.CartItemDto;
import com.example.Catering.dto.OrderRequestDto;
import com.example.Catering.entity.User;
import com.example.Catering.repository.MenuItemRepository;
import com.example.Catering.repository.UserRepository;
import com.example.Catering.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(MenuItemRepository menuItemRepository, OrderService orderService, UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/order")
    public String showOrderForm(Authentication authentication, HttpSession session, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List cart = (List) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/menu";
        }

        model.addAttribute("order", new OrderRequestDto());
        return "order-form";
    }

    @PostMapping("/order")
    public String createOrder(
            Authentication authentication,
            HttpSession session,
            @Valid OrderRequestDto orderDto,
            BindingResult result,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/menu";
        }

        // Заполняем orderDto из корзины
        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();
        for (CartItemDto item : cart) {
            OrderRequestDto.OrderItemDto dto = new OrderRequestDto.OrderItemDto();
            dto.setMenuItemId(item.getMenuItemId());
            dto.setQuantity(item.getQuantity());
            items.add(dto);
        }
        orderDto.setItems(items);

        if (result.hasErrors()) {
            return "order-form";
        }

        orderService.createOrder(user, orderDto);
        session.removeAttribute("cart"); // Очищаем корзину
        return "redirect:/profile?orderSuccess=true";
    }
}