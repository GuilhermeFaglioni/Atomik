package com.atomik.atomik_api.infrastructure.adapter;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenAdapter implements RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "refresh_token:";

    @Override
    public void save(String token, UUID userId, Long expirationInSeconds) {
        redisTemplate.opsForValue()
                .set(PREFIX + token, userId.toString(), Duration.ofSeconds(expirationInSeconds));
    }

    @Override
    public Optional<UUID> findUserIdByToken(String token) {
        String userId = redisTemplate.opsForValue().get(PREFIX + token);
        return Optional.ofNullable(userId).map(UUID::fromString);
    }

    @Override
    public void deleteByToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
