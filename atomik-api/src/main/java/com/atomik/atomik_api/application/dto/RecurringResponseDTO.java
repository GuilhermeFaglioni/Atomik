package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.atomik.atomik_api.domain.model.RecurringFrequency;
import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.model.TransactionType;

public record RecurringResponseDTO(
        String id,
        String categoryId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String description,
        TransactionType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime nextDueDate,
        RecurringFrequency frequency,
        RecurringStatus status) {
}
