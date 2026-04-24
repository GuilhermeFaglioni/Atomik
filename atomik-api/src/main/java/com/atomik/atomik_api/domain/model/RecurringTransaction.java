package com.atomik.atomik_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RecurringTransaction {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final UUID sourceAccountId;
    private final UUID destinationAccountId;
    private final BigDecimal amount;
    private final String description;
    private final TransactionType type;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime nextDueDate;
    private final RecurringFrequency frequency;
    private final RecurringStatus status;
    private final LocalDateTime createdAt;

    public RecurringTransaction(UUID id, UUID userId, UUID categoryId, UUID sourceAccountId,
            UUID destinationAccountId, BigDecimal amount, String description,
            TransactionType type, LocalDateTime startDate, LocalDateTime endDate,
            LocalDateTime nextDueDate, RecurringFrequency frequency,
            RecurringStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDueDate = nextDueDate;
        this.frequency = frequency;
        this.status = status;
        this.createdAt = createdAt;
        this.validate();
    }

    public static RecurringTransaction create(UUID userId, UUID categoryId, UUID sourceAccountId,
            UUID destinationAccountId, BigDecimal amount, String description,
            TransactionType type, RecurringFrequency frequency,
            LocalDateTime startDate, LocalDateTime endDate) {
        return new RecurringTransaction(
                UUID.randomUUID(),
                userId,
                categoryId,
                sourceAccountId,
                destinationAccountId,
                amount,
                description,
                type,
                startDate,
                endDate,
                startDate,
                frequency,
                RecurringStatus.ACTIVE,
                LocalDateTime.now());
    }

    private void validate() {
        if (id == null || userId == null || categoryId == null || sourceAccountId == null || amount == null
                || type == null || startDate == null || frequency == null || status == null || createdAt == null) {
            throw new IllegalArgumentException("Essential fields cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (type == TransactionType.TRANSFER && destinationAccountId == null) {
            throw new IllegalArgumentException("Transfer type requires a destination account");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    public RecurringTransaction updateStatus(RecurringTransaction recurringTransaction, RecurringStatus status) {
        return new RecurringTransaction(recurringTransaction.id, recurringTransaction.userId,
                recurringTransaction.categoryId,
                recurringTransaction.sourceAccountId, recurringTransaction.destinationAccountId,
                recurringTransaction.amount, recurringTransaction.description,
                recurringTransaction.type, recurringTransaction.startDate, recurringTransaction.endDate,
                recurringTransaction.nextDueDate, recurringTransaction.frequency,
                status, recurringTransaction.createdAt);
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

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getNextDueDate() {
        return nextDueDate;
    }

    public RecurringFrequency getFrequency() {
        return frequency;
    }

    public RecurringStatus getStatus() {
        return status;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
