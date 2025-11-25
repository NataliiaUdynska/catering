package com.example.Catering.controller;

import com.example.Catering.entity.MenuItem;
import com.example.Catering.entity.Order;
import com.example.Catering.repository.MenuItemRepository;
import com.example.Catering.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class AdminController {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public AdminController(MenuItemRepository menuItemRepository, OrderRepository orderRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
    }

    // ============= ЗАКАЗЫ =============

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("statuses", Order.OrderStatus.values()); // важно!
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            RedirectAttributes redirectAttributes) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с ID " + id + " не найден"));

        order.setStatus(status);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("message", "Статус заказа обновлён!");
        return "redirect:/admin/orders";
    }

    // ============= МЕНЮ =============

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("items", menuItemRepository.findAll());
        model.addAttribute("newItem", new MenuItem()); // важно!
        return "admin/menu";
    }

    @PostMapping("/menu")
    public String addMenuItem(
            @Valid @ModelAttribute("newItem") MenuItem item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении блюда");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newItem", bindingResult);
            redirectAttributes.addFlashAttribute("newItem", item);
            return "redirect:/admin/menu";
        }

        menuItemRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Блюдо добавлено!");
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!menuItemRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Блюдо уже удалено или не существует");
            return "redirect:/admin/menu";
        }
        menuItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Блюдо удалено!");
        return "redirect:/admin/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editMenuItemForm(@PathVariable Long id, Model model) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Блюдо с ID " + id + " не найдено"));
        model.addAttribute("item", item); // важно!
        return "admin/edit-menu-item";
    }

    @PostMapping("/menu/update")
    public String updateMenuItem(
            @Valid @ModelAttribute("item") MenuItem item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Передаём ошибки и объект обратно в форму — без редиректа!
            return "admin/edit-menu-item";
        }

        menuItemRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Блюдо обновлено!");
        return "redirect:/admin/menu";
    }
}