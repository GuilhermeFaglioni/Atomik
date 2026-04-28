package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AuthResponse;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.TokenService;

@Service
public class RefreshAuthTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public RefreshAuthTokenUseCase(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
            TokenService tokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthResponse execute(String refreshToken) {
        UUID userId = refreshTokenRepository.findUserIdByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        refreshTokenRepository.deleteByToken(refreshToken);

        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken();
        long accessTokenExpiresIn = tokenService.getAccessTokenExpiresInSeconds();
        long refreshTokenExpiresIn = tokenService.getRefreshTokenExpiresInSeconds();

        refreshTokenRepository.save(newRefreshToken, userId, refreshTokenExpiresIn);

        return new AuthResponse(newAccessToken, newRefreshToken, "Bearer", accessTokenExpiresIn);
    }
}
