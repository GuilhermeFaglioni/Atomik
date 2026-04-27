package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.service.TransactionAuditService;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.infrastructure.service.TransactionReconciliationServiceImpl;

@ExtendWith(MockitoExtension.class)
class CreateUniqueTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FinancialResourceOwnershipService financialResourceOwnershipService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionAuditService transactionAuditService;

    private CreateUniqueTransactionUseCase createUniqueTransactionUseCase;

    @BeforeEach
    void setUp() {
        createUniqueTransactionUseCase = new CreateUniqueTransactionUseCase(
                transactionRepository,
                financialResourceOwnershipService,
                new TransactionReconciliationServiceImpl(accountRepository),
                transactionAuditService);
    }

    @Test
    @DisplayName("Should apply expense balance before saving transaction")
    void shouldApplyExpenseBalanceBeforeSavingTransaction() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account sourceAccount = new Account(accountId, userId, "Wallet", AccountType.CHECKING, "BRL",
                LocalDateTime.now(), new BigDecimal("100.00"));
        Category category = new Category(categoryId, userId, "Food", "fork", "red", false);

        when(financialResourceOwnershipService.requireExistingUser(userId.toString())).thenReturn(userId);
        when(financialResourceOwnershipService.requireOwnedCategory(userId, categoryId.toString())).thenReturn(category);
        when(financialResourceOwnershipService.requireOwnedAccount(userId, accountId.toString(), "Account not found"))
                .thenReturn(sourceAccount);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        createUniqueTransactionUseCase.execute(userId.toString(), categoryId.toString(), accountId.toString(),
                new BigDecimal("20.00"), "Lunch", LocalDateTime.now(), TransactionType.EXPENSE);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).update(accountCaptor.capture());
        assertEquals(new BigDecimal("80.00"), accountCaptor.getValue().getBalance());
        verify(transactionRepository).save(any());
    }
}
