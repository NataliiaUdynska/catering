package com.example.Catering.controller;

import com.example.Catering.dto.ContactRequestDto;
import com.example.Catering.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InfoController {

    private final ContactService contactService;

    // 👇 Явный конструктор для внедрения зависимости
    public InfoController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("contactRequest", new ContactRequestDto());
        return "contacts";
    }

    @PostMapping("/contacts")
    public String submitContact(
            @Valid ContactRequestDto contactRequest,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "contacts";
        }

        contactService.saveContactRequest(contactRequest); // ← вызов метода
        model.addAttribute("success", true);
        model.addAttribute("contactRequest", new ContactRequestDto());
        return "contacts";
    }
}