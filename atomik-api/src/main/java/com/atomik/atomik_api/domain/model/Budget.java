package com.atomik.atomik_api.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Budget {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final BigDecimal limitAmount;
    private final Integer month;
    private final Integer year;
    private final String name;

    public static Budget createNewBudget(UUID userId, UUID categoryId, BigDecimal limitAmount, Integer month,
            Integer year, String name) {
        Budget budget = new Budget(UUID.randomUUID(), userId, categoryId, limitAmount, month, year, name);
        budget.validate();
        return budget;
    }

    public Budget(UUID id, UUID userId, UUID categoryId, BigDecimal limitAmount, Integer month, Integer year,
            String name) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.month = month;
        this.year = year;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Budget name is required");
        }
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
