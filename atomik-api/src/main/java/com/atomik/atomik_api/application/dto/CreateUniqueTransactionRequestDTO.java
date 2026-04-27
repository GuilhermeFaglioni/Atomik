package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atomik.atomik_api.domain.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUniqueTransactionRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Account ID is required") String accountId,
        @NotBlank(message = "Category ID is required") String categoryId,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Amount is required") BigDecimal amount,
        @NotNull(message = "Date is required") LocalDateTime date,
        @NotNull(message = "Type is required") TransactionType type) {
}
