package com.example.Catering.controller;

import com.example.Catering.dto.CartItemDto;
import com.example.Catering.dto.OrderRequestDto;
import com.example.Catering.entity.User;
import com.example.Catering.repository.UserRepository;
import com.example.Catering.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // ===== Показ формы заказа =====
    @GetMapping
    public String showOrderForm(Authentication authentication, HttpSession session, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/menu";
        }

        if (!model.containsAttribute("order")) {
            model.addAttribute("order", new OrderRequestDto());
        }
        return "order-form";
    }

    // ===== Создание заказа =====
    @PostMapping
    public String createOrder(
            Authentication authentication,
            HttpSession session,
            @Valid @ModelAttribute("order") OrderRequestDto orderDto,
            BindingResult result,
            Model model
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Получаем текущего пользователя
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем корзину
        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("errorMessage", "Корзина пуста");
            return "order-form";
        }

        // Проверка валидации формы
        if (result.hasErrors()) {
            model.addAttribute("order", orderDto);
            return "order-form";
        }

        // ===== Преобразуем корзину в позиции заказа =====
        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();
        for (CartItemDto cartItem : cart) {
            OrderRequestDto.OrderItemDto itemDto = new OrderRequestDto.OrderItemDto();
            itemDto.setMenuItemId(cartItem.getMenuItemId());
            itemDto.setQuantity(cartItem.getQuantity());
            items.add(itemDto);
        }
        orderDto.setItems(items);

        try {
            // ===== Создаем заказ =====
            orderService.createOrder(user, orderDto);

            // ===== Очистка корзины =====
            session.removeAttribute("cart");

            // ===== Перенаправление с сообщением об успехе =====
            return "redirect:/profile?orderSuccess=true";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Не удалось оформить заказ. Попробуйте позже.");
            model.addAttribute("order", orderDto);
            return "order-form";
        }
    }
}
