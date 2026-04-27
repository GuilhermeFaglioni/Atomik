package com.atomik.atomik_api.application.dto;

import com.atomik.atomik_api.domain.model.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Type is required") AccountType type,
        @NotBlank(message = "Currency is required") String currency) {
}
