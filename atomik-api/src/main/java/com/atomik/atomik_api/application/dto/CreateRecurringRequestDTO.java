package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atomik.atomik_api.domain.model.RecurringFrequency;
import com.atomik.atomik_api.domain.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRecurringRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Category ID is required") String categoryId,
        @NotBlank(message = "Source Account ID is required") String sourceAccountId,
        String destinationAccountId,
        @NotNull(message = "Amount is required") BigDecimal amount,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Type is required") TransactionType type,
        @NotNull(message = "Start date is required") LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull(message = "Frequency is required") RecurringFrequency frequency) {
}
