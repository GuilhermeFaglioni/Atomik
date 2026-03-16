package com.atomik.atomik_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {
    private String token;
    private UUID userId;
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
