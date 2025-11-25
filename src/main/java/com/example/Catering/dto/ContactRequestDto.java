package com.example.Catering.dto;

import jakarta.validation.constraints.NotBlank;

public class ContactRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String message;

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}