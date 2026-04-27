package com.atomik.atomik_api.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.UpdateUserRequestDTO;
import com.atomik.atomik_api.application.dto.UserResponseDTO;
import com.atomik.atomik_api.application.usecases.DeleteUserUseCase;
import com.atomik.atomik_api.application.usecases.GetUserUseCase;
import com.atomik.atomik_api.application.usecases.UpdateUserUseCase;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final AuthenticatedUserService authenticatedUserService;

    public UserController(GetUserUseCase getUserUseCase, UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase, AuthenticatedUserService authenticatedUserService) {
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String id, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, id);
        var response = getUserUseCase.execute(authenticatedUserId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String id,
            @RequestBody @Valid UpdateUserRequestDTO request, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, id);
        var response = updateUserUseCase.execute(authenticatedUserId, request.name(), request.email(),
                request.preferredCurrency());
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, id);
        deleteUserUseCase.execute(authenticatedUserId);
        return ResponseEntity.status(204).build();
    }
}
