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
public class User {
    private final UUID id;
    private final String name;
    private final Email email;
    private final String passwordHash;
    private final String preferredCurrency;
    private final LocalDateTime createdAt;

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
}
