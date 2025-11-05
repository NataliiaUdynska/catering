package com.example.Catering.controller;

import com.example.Catering.dto.CartItemDto;
import com.example.Catering.dto.OrderRequestDto;
import com.example.Catering.entity.User;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/order")
    public String showOrderForm(Authentication authentication, HttpSession session, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
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

        // 1. Проверка аутентификации
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 2. Получение пользователя
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            // При редиректе flash-сообщения не сохранятся без RedirectAttributes,
            // поэтому просто перенаправляем на логин
            return "redirect:/login";
        }

        // 3. Проверка корзины
        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/menu";
        }

        // 4. ВАЖНО: если есть ошибки валидации — вернуть форму с объектом 'order'
        if (result.hasErrors()) {
            model.addAttribute("order", orderDto); // ← ЭТО КРИТИЧЕСКИ ВАЖНО!
            return "order-form";
        }

        // 5. Заполнение позиций заказа
        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();
        for (CartItemDto cartItem : cart) {
            OrderRequestDto.OrderItemDto itemDto = new OrderRequestDto.OrderItemDto();
            itemDto.setMenuItemId(cartItem.getMenuItemId());
            itemDto.setName(cartItem.getName());
            itemDto.setPrice(BigDecimal.valueOf(cartItem.getPrice()));
            itemDto.setQuantity(cartItem.getQuantity());
            items.add(itemDto);
        }
        orderDto.setItems(items);

        try {
            // 6. Сохранение заказа
            orderService.createOrder(user, orderDto);

            // 7. Очистка корзины
            session.removeAttribute("cart");

            // 8. Прямой рендер страницы успеха (без редиректа)
            return "order-success";

        } catch (Exception e) {
            e.printStackTrace();
            // При ошибке возвращаем форму с текущими данными
            model.addAttribute("order", orderDto);
            model.addAttribute("errorMessage", "Не удалось оформить заказ. Попробуйте позже.");
            return "order-form";
        }
    }
}