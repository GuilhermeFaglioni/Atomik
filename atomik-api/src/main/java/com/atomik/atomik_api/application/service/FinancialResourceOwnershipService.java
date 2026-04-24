package com.atomik.atomik_api.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class FinancialResourceOwnershipService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public FinancialResourceOwnershipService(UserRepository userRepository, AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public UUID requireExistingUser(String userId) {
        UUID parsedUserId = UUID.fromString(userId);
        userRepository.findById(parsedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return parsedUserId;
    }

    public Category requireOwnedCategory(UUID userId, String categoryId) {
        Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!userId.equals(category.getUserId())) {
            throw new UnauthorizedException("User not authorized to access this category");
        }

        return category;
    }

    public Account requireOwnedAccount(UUID userId, String accountId, String notFoundMessage) {
        Account account = accountRepository.findById(UUID.fromString(accountId))
                .orElseThrow(() -> new AccountNotFoundException(notFoundMessage));

        if (!userId.equals(account.getUserId())) {
            throw new UnauthorizedException("User not authorized to access this account");
        }

        return account;
    }
}
