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
