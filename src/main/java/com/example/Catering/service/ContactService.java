package com.example.Catering.service;

import com.example.Catering.dto.ContactRequestDto;
import com.example.Catering.entity.ContactRequest;
import com.example.Catering.repository.ContactRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRequestRepository contactRequestRepository;

    //  Конструктор для репозитория
    public ContactService(ContactRequestRepository contactRequestRepository) {
        this.contactRequestRepository = contactRequestRepository;
    }

    public void saveContactRequest(ContactRequestDto dto) {
        ContactRequest entity = new ContactRequest();
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setMessage(dto.getMessage());
        contactRequestRepository.save(entity);
    }
}