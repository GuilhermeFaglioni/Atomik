package com.atomik.atomik_api.application.usecases;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Test
    @DisplayName("Should delete refresh token on logout")
    void shouldDeleteRefreshTokenOnLogout() {
        logoutUseCase.execute("refresh-token");

        verify(refreshTokenRepository).deleteByToken("refresh-token");
    }
}
