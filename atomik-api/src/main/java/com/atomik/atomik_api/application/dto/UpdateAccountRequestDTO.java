package com.atomik.atomik_api.application.dto;

import com.atomik.atomik_api.domain.model.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Currency is required") String currency,
        @NotNull(message = "Type is required") AccountType type) {
}
