package com.atomik.atomik_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {
    private String token;
    private UUID userId;
    private LocalDateTime expiresAt;

    public RefreshToken(String token, UUID userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
