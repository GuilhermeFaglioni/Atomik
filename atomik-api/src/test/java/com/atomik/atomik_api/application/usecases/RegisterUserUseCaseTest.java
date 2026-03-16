package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.PasswordHasherService;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasherService passwordHasherService;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Should create user when email is unique")
    void shouldCreateUserWhenEmailIsUnique() {
        // Arrange
        String email = "new@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordHasherService.hashPassword(any())).thenReturn("hashed_password");

        // Act
        var result = registerUserUseCase.execute("John Doe", email, "pass123", "BRL");

        // Assert
        assertNotNull(result);
        assertEquals("BRL", result.preferredCurrency());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Arrange
        String email = "exists@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.createNewUser("Existing", email, "hash", "USD")));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            registerUserUseCase.execute("John Doe", email, "pass123", "BRL");
        });
    }
}
