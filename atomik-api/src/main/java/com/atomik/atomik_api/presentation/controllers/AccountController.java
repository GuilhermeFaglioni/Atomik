package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.AccountCreatedResponse;
import com.atomik.atomik_api.application.dto.AccountResponse;
import com.atomik.atomik_api.application.dto.CreateAccountRequestDTO;
import com.atomik.atomik_api.application.dto.UpdateAccountRequestDTO;
import com.atomik.atomik_api.application.usecases.CreateAccountUseCase;
import com.atomik.atomik_api.application.usecases.DeleteAccountUseCase;
import com.atomik.atomik_api.application.usecases.GetAccountUseCase;
import com.atomik.atomik_api.application.usecases.ListAccountsUseCase;
import com.atomik.atomik_api.application.usecases.UpdateAccountUseCase;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final AuthenticatedUserService authenticatedUserService;

    public AccountController(CreateAccountUseCase createAccountUseCase, DeleteAccountUseCase deleteAccountUseCase,
            GetAccountUseCase getAccountUseCase, ListAccountsUseCase listAccountsUseCase,
            UpdateAccountUseCase updateAccountUseCase, AuthenticatedUserService authenticatedUserService) {
        this.createAccountUseCase = createAccountUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.listAccountsUseCase = listAccountsUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountCreatedResponse> createAccount(@RequestBody CreateAccountRequestDTO request,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, request.userId());
        var response = createAccountUseCase.execute(authenticatedUserId, request.name(), request.getType(),
                request.currency());
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{userId}/{id}")
    public void deleteAccount(@PathVariable String userId, @PathVariable String id, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        deleteAccountUseCase.execute(authenticatedUserId, id);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String userId, @PathVariable String id,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = getAccountUseCase.execute(authenticatedUserId, id);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AccountResponse>> listAccounts(@PathVariable String userId,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = listAccountsUseCase.execute(authenticatedUserId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable String userId, @PathVariable String id,
            @RequestBody UpdateAccountRequestDTO request, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = updateAccountUseCase.execute(authenticatedUserId, id, request.name(), request.currency(),
                request.getType());
        return ResponseEntity.status(200).body(response);
    }
}
