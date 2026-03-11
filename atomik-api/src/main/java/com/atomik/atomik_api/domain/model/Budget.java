package com.atomik.atomik_api.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;

import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Budget {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final BigDecimal limitAmount;
    private final Integer month;
    private final Integer year;
    private final LocalDateTime createdAt;

    public static Budget createNewBudget(UUID userId, UUID categoryId, BigDecimal limitAmount, Integer month,
            Integer year) {
        Budget budget = new Budget(UUID.randomUUID(), userId, categoryId, limitAmount, month, year,
                LocalDateTime.now());
        budget.validate();
        return budget;
    }

    public void validate() {
        if (limitAmount == null) {
            throw new IllegalArgumentException("Budget limitAmount is required");
        }
        if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget limitAmount must be greater than 0.0");
        }
        if (month == null) {
            throw new IllegalArgumentException("Budget month is required");
        }
        if (month <= 0 || month > 12) {
            throw new IllegalArgumentException("Month must be between or equal to 1 and 12");
        }
        if (year == null) {
            throw new IllegalArgumentException("Budget year is required");
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100");
        }
    }
}
