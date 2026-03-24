package com.atomik.atomik_api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email format invalid") String email,
        @NotBlank(message = "Password is required") String passwordHash,
        @NotBlank(message = "Preferred currency is required") String preferredCurrency) {
}
