package com.atomik.atomik_api.infrastructure.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.service.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtTokenService implements TokenService {
    private static final long ACCESS_TOKEN_EXPIRES_IN_SECONDS = 7200L;
    private static final long REFRESH_TOKEN_EXPIRES_IN_SECONDS = 604800L;

    @Value("${api.security.token.secret}")
    private String secret;

    @Override
    public String generateAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("atomik-api")
                    .withSubject(user.getId().toString())
                    .withClaim("email", user.getEmail().value())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error creating token", e);
        }
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String extractSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("atomik-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        return extractSubject(token) != null;
    }

    @Override
    public long getAccessTokenExpiresInSeconds() {
        return ACCESS_TOKEN_EXPIRES_IN_SECONDS;
    }

    @Override
    public long getRefreshTokenExpiresInSeconds() {
        return REFRESH_TOKEN_EXPIRES_IN_SECONDS;
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
