package com.atomik.atomik_api.domain.repository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<UUID> findUserIdByToken(String token);

    void save(String token, UUID userId, Long expirationInSeconds);

    void deleteByToken(String token);
}
