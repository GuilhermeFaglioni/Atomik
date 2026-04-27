package com.atomik.atomik_api.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FinancialResourceOwnershipServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FinancialResourceOwnershipService financialResourceOwnershipService;

    @Test
    @DisplayName("Should reject account owned by another user")
    void shouldRejectAccountOwnedByAnotherUser() {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID foreignUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account foreignAccount = new Account(accountId, foreignUserId, "Wallet", AccountType.CHECKING, "BRL", null,
                java.math.BigDecimal.ZERO);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(foreignAccount));

        assertThrows(UnauthorizedException.class, () -> financialResourceOwnershipService.requireOwnedAccount(
                authenticatedUserId,
                accountId.toString(),
                "Account not found"));
    }

    @Test
    @DisplayName("Should reject category owned by another user")
    void shouldRejectCategoryOwnedByAnotherUser() {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID foreignUserId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category foreignCategory = new Category(categoryId, foreignUserId, "Food", "fork", "red", false);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foreignCategory));

        assertThrows(UnauthorizedException.class,
                () -> financialResourceOwnershipService.requireOwnedCategory(authenticatedUserId, categoryId.toString()));
    }
}
