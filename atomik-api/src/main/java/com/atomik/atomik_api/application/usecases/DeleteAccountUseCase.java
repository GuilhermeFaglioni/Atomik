package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.DeleteAccountResponse;
import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;


@Service
public class DeleteAccountUseCase {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public DeleteAccountUseCase(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public DeleteAccountResponse execute(String userId, String accountId) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var account = accountRepository.findById(UUID.fromString(accountId))
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("You do not have permission to delete this account");
        }

        accountRepository.delete(account);

        return new DeleteAccountResponse(accountId, "Account deleted successfully");
    }
}
