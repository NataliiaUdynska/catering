package com.example.Catering.controller;

import com.example.Catering.entity.Invoice;
import com.example.Catering.entity.MenuItem;
import com.example.Catering.entity.Order;
import com.example.Catering.repository.InvoiceRepository;
import com.example.Catering.repository.MenuItemRepository;
import com.example.Catering.repository.OrderRepository;
import com.example.Catering.service.EmailService;
import com.example.Catering.service.PdfInvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;
    private final PdfInvoiceService pdfInvoiceService;

    @Autowired
    public AdminController(MenuItemRepository menuItemRepository,
                           OrderRepository orderRepository,
                           InvoiceRepository invoiceRepository,
                           EmailService emailService,
                           PdfInvoiceService pdfInvoiceService) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
        this.pdfInvoiceService = pdfInvoiceService;
    }

    // Панель управления
    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    // Меню управления
    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("items", menuItemRepository.findAll());
        model.addAttribute("newItem", new MenuItem());
        return "admin/menu";
    }

    @PostMapping("/menu")
    public String addMenuItem(@Valid @ModelAttribute("newItem") MenuItem item,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding menu item");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newItem", result);
            redirectAttributes.addFlashAttribute("newItem", item);
            return "redirect:/admin/menu";
        }
        menuItemRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Menu item added!");
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!menuItemRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu item does not exist");
            return "redirect:/admin/menu";
        }
        menuItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Menu item deleted!");
        return "redirect:/admin/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editMenuItemForm(@PathVariable Long id, Model model) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
        model.addAttribute("item", item);
        return "admin/edit-menu-item";
    }

    @PostMapping("/menu/update")
    public String updateMenuItem(@Valid @ModelAttribute("item") MenuItem item,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/edit-menu-item";
        }
        menuItemRepository.save(item);
        redirectAttributes.addFlashAttribute("message", "Menu item updated!");
        return "redirect:/admin/menu";
    }

    // Управление заказами
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String statusParam,
                                    RedirectAttributes redirectAttributes) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + id + " not found"));

        Order.OrderStatus status;
        try {
            status = Order.OrderStatus.valueOf(statusParam);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid status: " + statusParam);
            return "redirect:/admin/orders";
        }

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        orderRepository.save(order);

        // Генерация и отправка счёта при CONFIRMED
        if (oldStatus != Order.OrderStatus.CONFIRMED && status == Order.OrderStatus.CONFIRMED) {
            if (invoiceRepository.findByOrderId(id) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invoice already generated");
                return "redirect:/admin/orders";
            }

            Invoice invoice = new Invoice(order);
            invoice.setStatus("SENT");
            invoiceRepository.save(invoice);

            // Отправка email клиенту
            try {
                emailService.sendInvoiceEmail(invoice);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to send invoice email: " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("message", "Order status updated!");
        return "redirect:/admin/orders";
    }

    // Просмотр счет-фактуры
    @GetMapping("/orders/{orderId}/view-invoice")
    public String viewInvoice(@PathVariable Long orderId, Model model) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId);
        if (invoice == null) {
            model.addAttribute("errorMessage", "Invoice not found. Order status might not be CONFIRMED.");
            return "error";
        }
        model.addAttribute("invoice", invoice);
        return "admin/invoice"; // HTML preview
    }

    // Инвойс в PDF
    @GetMapping("/orders/{orderId}/invoice/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}