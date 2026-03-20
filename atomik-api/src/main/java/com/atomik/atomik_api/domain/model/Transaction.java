package com.atomik.atomik_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final UUID sourceAccountId;
    private final UUID destinationAccountId;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime date;
    private final TransactionType type;
    private final SyncStatusType syncStatus;
    private final LocalDateTime createdAt;

    public static Transaction createTransfer(UUID userId, UUID categoryId, UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount, String description, LocalDateTime date) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                userId,
                categoryId,
                sourceAccountId,
                destinationAccountId,
                amount,
                description,
                date,
                TransactionType.TRANSFER,
                SyncStatusType.PENDING,
                LocalDateTime.now());
        transaction.validate();
        return transaction;
    }

    public static Transaction createSingleEntry(UUID userId, UUID categoryId, UUID accountId, BigDecimal amount,
            String description, LocalDateTime date, TransactionType type) {
        if (type == TransactionType.TRANSFER) {
            throw new IllegalArgumentException("Use createTransfer for transfer type");
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                userId,
                categoryId,
                accountId,
                null,
                amount,
                description,
                date,
                type,
                SyncStatusType.PENDING,
                LocalDateTime.now());
        transaction.validate();
        return transaction;
    }

    public Transaction(UUID id, UUID userId, UUID categoryId, UUID sourceAccountId, UUID destinationAccountId,
            BigDecimal amount, String description, LocalDateTime date, TransactionType type, SyncStatusType syncStatus,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
        this.syncStatus = syncStatus;
        this.createdAt = createdAt;
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

    public LocalDateTime getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public SyncStatusType getSyncStatus() {
        return syncStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void validate() {
        if (userId == null)
            throw new IllegalArgumentException("User ID is required");
        if (categoryId == null)
            throw new IllegalArgumentException("Category ID is required");
        if (sourceAccountId == null)
            throw new IllegalArgumentException("Source Account ID is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Positive amount is required");
        if (date == null)
            throw new IllegalArgumentException("Date is required");
        if (type == null)
            throw new IllegalArgumentException("Transaction type is required");

        if (type == TransactionType.TRANSFER) {
            if (destinationAccountId == null)
                throw new IllegalArgumentException("Destination Account is required for transfers");
            if (sourceAccountId.equals(destinationAccountId))
                throw new IllegalArgumentException("Source and Destination accounts must be different");
        }
    }
}
