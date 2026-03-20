package com.atomik.atomik_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Account {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private final AccountType type;
    private final String currency;
    private final LocalDateTime createdAt;
    private final BigDecimal balance;

    public static Account createNewAccount(UUID userId, String name, AccountType type, String currency) {
        Account account = new Account(
                UUID.randomUUID(),
                userId,
                name,
                type,
                currency != null ? currency : "BRL",
                LocalDateTime.now(),
                BigDecimal.ZERO);
        account.validate();
        return account;
    }

    public Account(UUID id, UUID userId, String name, AccountType type, String currency, LocalDateTime createdAt,
            BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.createdAt = createdAt;
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void validate() {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Account type is required");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Account currency is required");
        }
    }

    public Account deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BigDecimal newBalance = this.balance.add(amount);
        Account updatedAccount = new Account(this.id, this.userId, this.name, this.type, this.currency, this.createdAt,
                newBalance);
        return updatedAccount;
    }

    public Account withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BigDecimal newBalance = this.balance.subtract(amount);
        Account updatedAccount = new Account(this.id, this.userId, this.name, this.type, this.currency, this.createdAt,
                newBalance);
        return updatedAccount;
    }
}
