package com.atomik.atomik_api.domain.service;

import com.atomik.atomik_api.domain.model.User;

public interface TokenService {
    String generateAccessToken(User user);

    String generateRefreshToken();

    String extractSubject(String token);

    boolean validateToken(String token);

    long getAccessTokenExpiresInSeconds();

    long getRefreshTokenExpiresInSeconds();
}
