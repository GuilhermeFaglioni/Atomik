package com.atomik.atomik_api.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.AuthResponse;
import com.atomik.atomik_api.application.dto.LoginRequestDTO;
import com.atomik.atomik_api.application.dto.RefreshTokenRequestDTO;
import com.atomik.atomik_api.application.dto.RegisterRequestDTO;
import com.atomik.atomik_api.application.dto.UserCreatedResponse;
import com.atomik.atomik_api.application.usecases.AuthenticateUserUseCase;
import com.atomik.atomik_api.application.usecases.LogoutUseCase;
import com.atomik.atomik_api.application.usecases.RefreshAuthTokenUseCase;
import com.atomik.atomik_api.application.usecases.RegisterUserUseCase;
import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshAuthTokenUseCase refreshAuthTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, RegisterUserUseCase registerUserUseCase,
            RefreshAuthTokenUseCase refreshAuthTokenUseCase, LogoutUseCase logoutUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.refreshAuthTokenUseCase = refreshAuthTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO request)
            throws UnauthorizedException {
        var response = authenticateUserUseCase.execute(request.email(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserCreatedResponse> register(@RequestBody @Valid RegisterRequestDTO request)
            throws EmailAlreadyExistsException {
        var response = registerUserUseCase.execute(request.name(), request.email(), request.password(),
                request.preferredCurrency());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(refreshAuthTokenUseCase.execute(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequestDTO request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

}
