package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AccountResponse;
import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateAccountUseCase {
        private final AccountRepository accountRepository;
        private final UserRepository userRepository;

        public AccountResponse execute(String userId, String id, String name, String currency, AccountType type) {
                userRepository.findById(UUID.fromString(userId))
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                var existingAccount = accountRepository.findById(UUID.fromString(id))
                                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

                if (!existingAccount.getUserId().equals(UUID.fromString(userId))) {
                        throw new UnauthorizedException("You do not have permission to update this account");
                }

                var updatedAccount = existingAccount.toBuilder()
                                .name(name)
                                .currency(currency)
                                .type(type)
                                .build();

                updatedAccount.validate();
                var savedAccount = accountRepository.update(updatedAccount)
                                .orElseThrow(() -> new RuntimeException("Error updating account"));

                return toResponse(savedAccount);
        }

        private AccountResponse toResponse(Account account) {
                return new AccountResponse(
                                account.getId().toString(),
                                account.getName(),
                                account.getCurrency(),
                                account.getType().toString());
        }
}
