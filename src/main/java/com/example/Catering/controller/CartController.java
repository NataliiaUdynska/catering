package com.example.Catering.controller;

import com.example.Catering.dto.CartItemDto;
import com.example.Catering.entity.MenuItem;
import com.example.Catering.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class CartController {

    private final MenuItemRepository menuItemRepository;

    public CartController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItemDto> cart = getCartFromSession(session);

        double total = cart.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    // Обработка автоматического обновления количества
    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam Long menuItemId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        List<CartItemDto> cart = getCartFromSession(session);

        for (CartItemDto item : cart) {
            if (item.getMenuItemId().equals(menuItemId)) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    cart.remove(item);
                }
                break;
            }
        }

        if (cart.isEmpty()) {
            session.removeAttribute("cart");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long menuItemId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<CartItemDto> cart = getCartFromSession(session);

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
        List<CartItemDto> cart = getCartFromSession(session);
        cart.removeIf(item -> item.getMenuItemId().equals(menuItemId));

        if (cart.isEmpty()) session.removeAttribute("cart");
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }

    // Хелпер для получения корзины
    private List<CartItemDto> getCartFromSession(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        cart.removeIf(Objects::isNull);
        return cart;
    }
}