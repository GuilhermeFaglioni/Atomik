package com.atomik.atomik_api.application.usecases;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AuthResponse;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.PasswordHasherService;
import com.atomik.atomik_api.domain.service.TokenService;

@Service
public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordHasherService passwordHasherService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticateUserUseCase(UserRepository userRepository, TokenService tokenService, PasswordHasherService passwordHasherService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordHasherService = passwordHasherService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponse execute(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid Credentials"));

        if (!passwordHasherService.verifyPassword(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid Credentials");
        }

        String access = tokenService.generateAccessToken(user);
        String refresh = tokenService.generateRefreshToken();
        long accessTokenExpiresIn = tokenService.getAccessTokenExpiresInSeconds();
        long refreshTokenExpiresIn = tokenService.getRefreshTokenExpiresInSeconds();

        refreshTokenRepository.save(refresh, user.getId(), refreshTokenExpiresIn);

        return new AuthResponse(access, refresh, "Bearer", accessTokenExpiresIn);
    }
}
