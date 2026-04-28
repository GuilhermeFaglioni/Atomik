package com.atomik.atomik_api.application.usecases;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;

@Service
public class LogoutUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
