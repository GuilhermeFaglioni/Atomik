package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AccountCreatedResponse;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public CreateAccountUseCase(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountCreatedResponse execute(String userId, String name, AccountType type, String currency) {
        if (accountRepository.existsByNameAndUserId(name, UUID.fromString(userId))) {
            throw new IllegalArgumentException("Account already exists");
        }
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var account = Account.createNewAccount(UUID.fromString(userId), name, type, currency);
        accountRepository.save(account);

        return toResponse(account);
    }

    public AccountCreatedResponse toResponse(Account account) {
        return new AccountCreatedResponse(account.getId().toString(), account.getName(), account.getCurrency(),
                account.getType().toString());
    }
}
