package com.atomik.atomik_api.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.SyncStatusType;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class TransactionReconciliationServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should deposit revenue into source account")
    void shouldDepositRevenueIntoSourceAccount() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Transaction transaction = new Transaction(UUID.randomUUID(), userId, UUID.randomUUID(), accountId, null,
                new BigDecimal("25.00"), "Salary", LocalDateTime.now(), TransactionType.REVENUE,
                SyncStatusType.PENDING, createdAt);
        Account account = new Account(accountId, userId, "Main", AccountType.CHECKING, "BRL", createdAt,
                new BigDecimal("100.00"));
        TransactionReconciliationServiceImpl service = new TransactionReconciliationServiceImpl(accountRepository);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        service.apply(transaction);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).update(accountCaptor.capture());
        assertEquals(new BigDecimal("125.00"), accountCaptor.getValue().getBalance());
    }

    @Test
    @DisplayName("Should rollback transfer on both accounts")
    void shouldRollbackTransferOnBothAccounts() {
        UUID userId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Transaction transaction = new Transaction(UUID.randomUUID(), userId, UUID.randomUUID(), sourceAccountId,
                destinationAccountId, new BigDecimal("20.00"), "Transfer", LocalDateTime.now(),
                TransactionType.TRANSFER, SyncStatusType.PENDING, createdAt);
        Account sourceAccount = new Account(sourceAccountId, userId, "Checking", AccountType.CHECKING, "BRL",
                createdAt, new BigDecimal("80.00"));
        Account destinationAccount = new Account(destinationAccountId, userId, "Savings", AccountType.SAVINGS, "BRL",
                createdAt, new BigDecimal("70.00"));
        TransactionReconciliationServiceImpl service = new TransactionReconciliationServiceImpl(accountRepository);

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        service.rollBack(transaction);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, org.mockito.Mockito.times(2)).update(accountCaptor.capture());
        List<Account> updatedAccounts = accountCaptor.getAllValues();
        assertEquals(new BigDecimal("100.00"), updatedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("50.00"), updatedAccounts.get(1).getBalance());
    }
}
