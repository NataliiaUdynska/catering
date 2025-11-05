package com.example.Catering.controller;

import com.example.Catering.dto.CartItemDto;
import com.example.Catering.entity.MenuItem;
import com.example.Catering.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class CartController {

    private final MenuItemRepository menuItemRepository;

    public CartController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        } else {
            cart.removeIf(Objects::isNull);
        }
        session.setAttribute("cart", cart);

        double total = cart.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long menuItemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));

        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        cart.removeIf(Objects::isNull);

        boolean exists = false;
        for (CartItemDto dto : cart) {
            if (dto.getMenuItemId().equals(menuItemId)) {
                dto.setQuantity(dto.getQuantity() + quantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            CartItemDto dto = new CartItemDto();
            dto.setMenuItemId(item.getId());
            dto.setName(item.getName());
            dto.setPrice(item.getPrice().doubleValue());
            dto.setQuantity(quantity);
            cart.add(dto);
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long menuItemId, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item != null && item.getMenuItemId().equals(menuItemId));
            if (cart.isEmpty()) {
                session.removeAttribute("cart");
            } else {
                session.setAttribute("cart", cart);
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }

    // AJAX-обновление количества (опционально — можно убрать, если не используете)
    @PostMapping("/cart/update-ajax")
    @ResponseBody
    public ResponseEntity<String> updateQuantityAjax(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long menuItemId = ((Number) payload.get("menuItemId")).longValue();
        int quantity = ((Number) payload.get("quantity")).intValue();

        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null) {
            return ResponseEntity.badRequest().body("Корзина пуста");
        }

        List<CartItemDto> updated = new ArrayList<>();
        boolean found = false;
        for (CartItemDto item : cart) {
            if (item == null) continue;
            if (item.getMenuItemId().equals(menuItemId)) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                    updated.add(item);
                }
                found = true;
            } else {
                updated.add(item);
            }
        }
        if (!found) {
            return ResponseEntity.badRequest().body("Блюдо не найдено");
        }

        if (updated.isEmpty()) {
            session.removeAttribute("cart");
        } else {
            session.setAttribute("cart", updated);
        }

        return ResponseEntity.ok("OK");
    }
}