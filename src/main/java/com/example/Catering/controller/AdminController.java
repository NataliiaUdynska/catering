package com.example.Catering.controller;

import com.example.Catering.entity.Invoice;
import com.example.Catering.entity.MenuItem;
import com.example.Catering.entity.Order;
import com.example.Catering.repository.InvoiceRepository;
import com.example.Catering.repository.MenuItemRepository;
import com.example.Catering.repository.OrderRepository;
import com.example.Catering.service.EmailService;
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
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

    @Autowired
    public AdminController(MenuItemRepository menuItemRepository, OrderRepository orderRepository,
                           InvoiceRepository invoiceRepository, EmailService emailService) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String statusParam,
            RedirectAttributes redirectAttributes) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с ID " + id + " не найден"));

        Order.OrderStatus status;
        try {
            status = Order.OrderStatus.valueOf(statusParam);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Недопустимый статус: " + statusParam);
            return "redirect:/admin/orders";
        }

        Order.OrderStatus oldStatus = order.getStatus();

        order.setStatus(status);
        orderRepository.save(order);

        // Генерация и отправка счёта при изменении статуса на CONFIRMED
        if (oldStatus != Order.OrderStatus.CONFIRMED && status == Order.OrderStatus.CONFIRMED) {
            Invoice invoice = new Invoice(order);
            invoice.setStatus("SENT");
            invoiceRepository.save(invoice);

            // Отправка email клиенту
            emailService.sendInvoiceEmail(invoice);

            // Перенаправление на страницу просмотра счёта
            return "redirect:/admin/orders/" + id + "/view-invoice";
        }

        redirectAttributes.addFlashAttribute("message", "Статус заказа обновлён!");
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/{orderId}/view-invoice")
    public String viewInvoice(@PathVariable Long orderId, Model model) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId);
        if (invoice == null) {
            model.addAttribute("errorMessage", "Счёт не найден");
            return "error";
        }
        model.addAttribute("invoice", invoice);
        return "admin/invoice";
    }

    // ============= УПРАВЛЕНИЕ МЕНЮ =============
    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("items", menuItemRepository.findAll());
        model.addAttribute("newItem", new MenuItem());
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
            redirectAttributes.addFlashAttribute("errorMessage", "Блюдо не существует");
            return "redirect:/admin/menu";
        }
        menuItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Блюдо удалено!");
        return "redirect:/admin/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editMenuItemForm(@PathVariable Long id, Model model) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Блюдо не найдено"));
        model.addAttribute("item", item);
        return "admin/edit-menu-item";
    }

    @PostMapping("/menu/update")
    public String updateMenuItem(
            @Valid @ModelAttribute("item") MenuItem item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/edit-menu-item"; // Остаемся на той же странице
        }

        menuItemRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Блюдо обновлено!");
        return "redirect:/admin/menu";
    }
}