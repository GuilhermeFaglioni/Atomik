package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.infrastructure.service.TransactionReconciliationServiceImpl;

@ExtendWith(MockitoExtension.class)
class CreateTransferUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FinancialResourceOwnershipService financialResourceOwnershipService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionAuditService transactionAuditService;

    private CreateTransferUseCase createTransferUseCase;

    @BeforeEach
    void setUp() {
        createTransferUseCase = new CreateTransferUseCase(
                transactionRepository,
                financialResourceOwnershipService,
                new TransactionReconciliationServiceImpl(accountRepository),
                transactionAuditService);
    }

    @Test
    @DisplayName("Should move balance between owned accounts")
    void shouldMoveBalanceBetweenOwnedAccounts() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();
        Account sourceAccount = new Account(sourceAccountId, userId, "Checking", AccountType.CHECKING, "BRL",
                LocalDateTime.now(), new BigDecimal("100.00"));
        Account destinationAccount = new Account(destinationAccountId, userId, "Savings", AccountType.SAVINGS, "BRL",
                LocalDateTime.now(), new BigDecimal("50.00"));
        Category category = new Category(categoryId, userId, "Transfer", "arrows", "blue", false);

        when(financialResourceOwnershipService.requireExistingUser(userId.toString())).thenReturn(userId);
        when(financialResourceOwnershipService.requireOwnedCategory(userId, categoryId.toString())).thenReturn(category);
        when(financialResourceOwnershipService.requireOwnedAccount(userId, sourceAccountId.toString(),
                "Source account not found")).thenReturn(sourceAccount);
        when(financialResourceOwnershipService.requireOwnedAccount(userId, destinationAccountId.toString(),
                "Destination account not found")).thenReturn(destinationAccount);
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.update(any(Account.class))).thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        createTransferUseCase.execute(userId.toString(), categoryId.toString(), sourceAccountId.toString(),
                destinationAccountId.toString(), new BigDecimal("20.00"), "Move", LocalDateTime.now());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, org.mockito.Mockito.times(2)).update(accountCaptor.capture());
        List<Account> updatedAccounts = accountCaptor.getAllValues();
        assertEquals(new BigDecimal("80.00"), updatedAccounts.get(0).getBalance());
        assertEquals(new BigDecimal("70.00"), updatedAccounts.get(1).getBalance());
        verify(transactionRepository).save(any());
    }
}
