package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.model.Email;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.TokenService;

@ExtendWith(MockitoExtension.class)
class RefreshAuthTokenUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private RefreshAuthTokenUseCase refreshAuthTokenUseCase;

    @Test
    @DisplayName("Should rotate refresh token and return new auth response")
    void shouldRotateRefreshTokenAndReturnNewAuthResponse() {
        String refreshToken = "old-refresh-token";
        String newRefreshToken = "new-refresh-token";
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John", new Email("john@test.com"), "hash", "BRL", LocalDateTime.now());

        when(refreshTokenRepository.findUserIdByToken(refreshToken)).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(user)).thenReturn("new-access-token");
        when(tokenService.generateRefreshToken()).thenReturn(newRefreshToken);
        when(tokenService.getAccessTokenExpiresInSeconds()).thenReturn(7200L);
        when(tokenService.getRefreshTokenExpiresInSeconds()).thenReturn(604800L);

        var response = refreshAuthTokenUseCase.execute(refreshToken);

        assertEquals("new-access-token", response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());
        assertEquals(7200L, response.expiresIn());
        verify(refreshTokenRepository).deleteByToken(refreshToken);
        verify(refreshTokenRepository).save(newRefreshToken, userId, 604800L);
    }

    @Test
    @DisplayName("Should throw unauthorized when refresh token does not exist")
    void shouldThrowUnauthorizedWhenRefreshTokenDoesNotExist() {
        when(refreshTokenRepository.findUserIdByToken("missing-token")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> refreshAuthTokenUseCase.execute("missing-token"));
    }
}
