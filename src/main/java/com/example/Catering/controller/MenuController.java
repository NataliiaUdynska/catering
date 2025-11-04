package com.example.Catering.controller;

import com.example.Catering.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAllByOrderByNameAsc());
        return "menu";
    }
}