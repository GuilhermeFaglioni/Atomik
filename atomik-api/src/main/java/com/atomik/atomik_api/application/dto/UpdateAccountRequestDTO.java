package com.atomik.atomik_api.application.dto;

import com.atomik.atomik_api.domain.model.AccountType;

public record UpdateAccountRequestDTO(String name, String currency, String type) {
    public AccountType getType() {
        try {
            return AccountType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + type);
        }
    }
}
