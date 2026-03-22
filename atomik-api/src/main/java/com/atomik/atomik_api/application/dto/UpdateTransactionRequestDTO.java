package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atomik.atomik_api.domain.model.TransactionType;

public record UpdateTransactionRequestDTO(String categoryId, String sourceAccountId,
        String destinationAccountId, BigDecimal amount, String description, String date, String type) {
    public TransactionType getType() {
        try {
            return TransactionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }

    public LocalDateTime getDate() {
        try {
            return LocalDateTime.parse(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }
    }
}
