package com.atomik.atomik_api.application.dto;

import com.atomik.atomik_api.domain.model.AccountType;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Currency is required") String currency,
        @NotBlank(message = "Type is required") String type) {
    public AccountType getType() {
        try {
            return AccountType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + type);
        }
    }
}
