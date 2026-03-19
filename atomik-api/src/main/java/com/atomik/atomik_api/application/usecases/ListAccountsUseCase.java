package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AccountResponse;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListAccountsUseCase {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<AccountResponse> execute(String userId) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var accounts = accountRepository.findByUserId(UUID.fromString(userId));

        return accounts.stream().map(this::toResponse).toList();
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId().toString(), account.getName(), account.getCurrency(),
                account.getType().toString());
    }
}
