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

import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AccountType;
import com.atomik.atomik_api.domain.model.SyncStatusType;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.infrastructure.service.TransactionReconciliationImpService;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AccountRepository accountRepository;

    private DeleteTransactionUseCase deleteTransactionUseCase;

    @BeforeEach
    void setUp() {
        deleteTransactionUseCase = new DeleteTransactionUseCase(
                transactionRepository,
                userRepository,
                new TransactionReconciliationImpService(accountRepository),
                auditLogRepository);
    }

    @Test
    @DisplayName("Should rollback account balance before deleting transaction")
    void shouldRollbackAccountBalanceBeforeDeletingTransaction() {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        User user = new User(userId, "John", new com.atomik.atomik_api.domain.model.Email("john@test.com"), "hash",
                "BRL", createdAt);
        Transaction transaction = new Transaction(transactionId, userId, categoryId, accountId, null,
                new BigDecimal("20.00"), "Lunch", LocalDateTime.now().minusHours(1), TransactionType.EXPENSE,
                SyncStatusType.PENDING, createdAt);
        Account account = new Account(accountId, userId, "Wallet", AccountType.CHECKING, "BRL", createdAt,
                new BigDecimal("80.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        deleteTransactionUseCase.execute(userId.toString(), transactionId.toString());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).update(accountCaptor.capture());
        assertEquals(new BigDecimal("100.00"), accountCaptor.getValue().getBalance());
        verify(transactionRepository).delete(transaction);
    }
}
