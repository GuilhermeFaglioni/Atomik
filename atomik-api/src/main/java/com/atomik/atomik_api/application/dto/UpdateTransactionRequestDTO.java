package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atomik.atomik_api.domain.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionRequestDTO(
        @NotBlank(message = "Category ID is required") String categoryId,
        @NotBlank(message = "Source account ID is required") String sourceAccountId,
        String destinationAccountId,
        @NotNull(message = "Amount is required") BigDecimal amount,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Date is required") LocalDateTime date,
        @NotNull(message = "Type is required") TransactionType type) {
}
