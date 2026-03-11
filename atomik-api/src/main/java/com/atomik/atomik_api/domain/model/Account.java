package com.atomik.atomik_api.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private final AccountType type;
    private final String currency;
    private final LocalDateTime createdAt;

    public static Account createNewAccount(UUID userId, String name, AccountType type, String currency) {
        Account account = new Account(
                UUID.randomUUID(),
                userId,
                name,
                type,
                currency != null ? currency : "BRL",
                LocalDateTime.now());
        account.validate();
        return account;
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
}
