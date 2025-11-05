package com.example.Catering.service;

import com.example.Catering.dto.OrderRequestDto;
import com.example.Catering.entity.MenuItem;
import com.example.Catering.entity.Order;
import com.example.Catering.entity.OrderItem;
import com.example.Catering.entity.User;
import com.example.Catering.repository.MenuItemRepository;
import com.example.Catering.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Order createOrder(User user, OrderRequestDto dto) {
        Order order = new Order();
        order.setUser(user);
        order.setEventDateTime(dto.getEventDateTime());
        order.setNumberOfGuests(dto.getNumberOfGuests());

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequestDto.OrderItemDto itemDto : dto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Блюдо не найдено: " + itemDto.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
}