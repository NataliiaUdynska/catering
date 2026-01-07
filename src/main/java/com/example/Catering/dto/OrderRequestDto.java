package com.example.Catering.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRequestDto {

    @NotNull(message = "Укажите дату и время мероприятия")
    private LocalDateTime eventDateTime;

    @Min(value = 1, message = "Количество гостей должно быть не менее 1")
    @NotNull(message = "Укажите количество гостей")
    private Integer numberOfGuests;

    @NotBlank(message = "Адрес мероприятия обязателен")
    private String deliveryAddress;   // 🔥 изменено с eventAddress

    private String comment;           // 🔥 изменено с notes

    private List<OrderItemDto> items = new ArrayList<>();

    // ===== Геттеры и сеттеры =====
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }

    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    // ===== Вложенный класс для позиций заказа =====
    public static class OrderItemDto {
        private Long menuItemId;
        private String name;
        private BigDecimal price;
        private Integer quantity;

        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
