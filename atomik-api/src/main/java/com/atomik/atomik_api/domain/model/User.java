package com.atomik.atomik_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String name;
    private final Email email;
    private final String passwordHash;
    private final String preferredCurrency;
    private final LocalDateTime createdAt;

    public User(UUID id, String name, Email email, String passwordHash, String preferredCurrency,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.preferredCurrency = preferredCurrency;
        this.createdAt = createdAt;
    }

    public static User createNewUser(String name, String email, String passwordHash, String preferredCurrency) {
        User user = new User(
                UUID.randomUUID(),
                name,
                new Email(email),
                passwordHash,
                preferredCurrency != null ? preferredCurrency : "BRL",
                LocalDateTime.now());
        user.validate();
        return user;
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || !email.value().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
