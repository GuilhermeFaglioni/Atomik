package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;

    @PostMapping("/create")
    public AccountCreatedResponse createAccount(@RequestBody CreateAccountRequestDTO request) {
        return createAccountUseCase.execute(request.userId(), request.name(), request.getType(),
                request.currency());
    }

    @DeleteMapping("/{userId}/{id}")
    public void deleteAccount(@PathVariable String userId, @PathVariable String id) {
        deleteAccountUseCase.execute(userId, id);
    }

    @GetMapping("/{userId}/{id}")
    public AccountResponse getAccount(@PathVariable String userId, @PathVariable String id) {
        return getAccountUseCase.execute(userId, id);
    }

    @GetMapping("/{userId}")
    public List<AccountResponse> listAccounts(@PathVariable String userId) {
        return listAccountsUseCase.execute(userId);
    }

    @PutMapping("/{userId}/{id}")
    public AccountResponse updateAccount(@PathVariable String userId, @PathVariable String id,
            @RequestBody UpdateAccountRequestDTO request) {
        return updateAccountUseCase.execute(userId, id, request.name(), request.currency(), request.getType());
    }
}
