package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.RefreshTokenRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.PasswordHasherService;
import com.atomik.atomik_api.domain.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordHasherService passwordHasherService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        // Arrange
        String email = "user@test.com";
        String pass = "correct_pass";
        var user = User.createNewUser("User", email, "hashed_pass", "BRL");
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasherService.verifyPassword(pass, "hashed_pass")).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn("access_token");
        when(tokenService.generateRefreshToken()).thenReturn("refresh_token");

        // Act
        var result = authenticateUserUseCase.execute(email, pass);

        // Assert
        assertNotNull(result);
        assertEquals("access_token", result.accessToken());
        assertEquals("refresh_token", result.refreshToken());
        verify(refreshTokenRepository).save(anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("Should throw Unauthorized when user not found")
    void shouldThrowUnauthorizedWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            authenticateUserUseCase.execute("unknown@test.com", "any");
        });
    }

    @Test
    @DisplayName("Should throw Unauthorized when password is wrong")
    void shouldThrowUnauthorizedWhenPasswordIsWrong() {
        // Arrange
        String email = "user@test.com";
        var user = User.createNewUser("User", email, "hashed_pass", "BRL");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHasherService.verifyPassword(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            authenticateUserUseCase.execute(email, "wrong_pass");
        });
    }
}
