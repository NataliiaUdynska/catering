package com.example.Catering.controller;

import com.example.Catering.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/menu")
    public String menu(
            @RequestParam(value = "category", required = false, defaultValue = "all") String category,
            Model model) {

        // Проверяем, выбрана ли категория все или фильтр не задан
        if (category == null || category.equalsIgnoreCase("all")) {
            model.addAttribute("menuItems", menuItemRepository.findAllByOrderByNameAsc());
            model.addAttribute("selectedCategory", "all");
        } else {
            // Ищем в БД
            model.addAttribute("menuItems", menuItemRepository.findByCategoryIgnoreCaseOrderByNameAsc(category));
            model.addAttribute("selectedCategory", category);
        }

        return "menu";
    }
}