package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.model.SyncStatusType;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.infrastructure.service.TransactionReconciliationImpService;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private FinancialResourceOwnershipService financialResourceOwnershipService;

    @Mock
    private AccountRepository accountRepository;

    private UpdateTransactionUseCase updateTransactionUseCase;

    @BeforeEach
    void setUp() {
        updateTransactionUseCase = new UpdateTransactionUseCase(
                transactionRepository,
                auditLogRepository,
                new TransactionReconciliationImpService(accountRepository),
                financialResourceOwnershipService);
    }

    @Test
    @DisplayName("Should allow null destination account for non transfer and reconcile balance")
    void shouldAllowNullDestinationAccountForNonTransferAndReconcileBalance() {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Transaction currentTransaction = new Transaction(transactionId, userId, categoryId, accountId, null,
                new BigDecimal("10.00"), "Coffee", LocalDateTime.now().minusHours(1), TransactionType.EXPENSE,
                SyncStatusType.PENDING, createdAt);
        Account currentAccount = new Account(accountId, userId, "Wallet", AccountType.CHECKING, "BRL",
                createdAt, new BigDecimal("90.00"));
        Category category = new Category(categoryId, userId, "Food", "fork", "red", false);
        AtomicReference<Account> persistedAccount = new AtomicReference<>(currentAccount);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(currentTransaction));
        when(financialResourceOwnershipService.requireExistingUser(userId.toString())).thenReturn(userId);
        when(financialResourceOwnershipService.requireOwnedCategory(userId, categoryId.toString())).thenReturn(category);
        when(financialResourceOwnershipService.requireOwnedAccount(userId, accountId.toString(), "Source account not found"))
                .thenReturn(currentAccount);
        when(accountRepository.findById(accountId)).thenAnswer(invocation -> Optional.of(persistedAccount.get()));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> {
            Account updatedAccount = invocation.getArgument(0);
            persistedAccount.set(updatedAccount);
            return Optional.of(updatedAccount);
        });
        when(transactionRepository.update(any(Transaction.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        updateTransactionUseCase.execute(transactionId.toString(), userId.toString(), categoryId.toString(),
                accountId.toString(), null, new BigDecimal("20.00"), "Lunch", LocalDateTime.now(),
                TransactionType.EXPENSE);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).update(transactionCaptor.capture());
        assertNull(transactionCaptor.getValue().getDestinationAccountId());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, org.mockito.Mockito.times(2)).update(accountCaptor.capture());
        List<Account> updatedAccounts = accountCaptor.getAllValues();
        assertEquals(new BigDecimal("100.00"), updatedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("80.00"), updatedAccounts.get(1).getBalance());
    }
}
