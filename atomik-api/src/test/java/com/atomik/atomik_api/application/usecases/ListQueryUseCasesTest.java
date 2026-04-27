package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ListQueryUseCasesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ListUserBudgetsUseCase listUserBudgetsUseCase;

    @InjectMocks
    private ListUserCategoriesUseCase listUserCategoriesUseCase;

    @InjectMocks
    private ListAllUserAuditLogs listAllUserAuditLogs;

    @InjectMocks
    private GetRecurringTransactionsByUserUseCase getRecurringTransactionsByUserUseCase;

    @InjectMocks
    private ListUserTransactionUseCase listUserTransactionUseCase;

    @Test
    @DisplayName("List query use cases should return empty list instead of exception")
    void listQueryUseCasesShouldReturnEmptyListInsteadOfException() {
        String userId = UUID.randomUUID().toString();
        User user = User.createNewUser("John", "john@test.com", "hash", "BRL");

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
        when(budgetRepository.findAllByUserId(UUID.fromString(userId))).thenReturn(List.of());
        when(categoryRepository.findAllByUserId(UUID.fromString(userId))).thenReturn(List.of());
        when(auditLogRepository.findByUserId(userId)).thenReturn(List.of());
        when(recurringTransactionRepository.findByUserId(UUID.fromString(userId))).thenReturn(List.of());
        when(transactionRepository.findByUserId(UUID.fromString(userId))).thenReturn(List.of());

        assertTrue(assertDoesNotThrow(() -> listUserBudgetsUseCase.execute(userId)).isEmpty());
        assertTrue(assertDoesNotThrow(() -> listUserCategoriesUseCase.execute(userId)).isEmpty());
        assertTrue(assertDoesNotThrow(() -> listAllUserAuditLogs.execute(userId)).isEmpty());
        assertTrue(assertDoesNotThrow(() -> getRecurringTransactionsByUserUseCase.execute(userId)).isEmpty());
        assertTrue(assertDoesNotThrow(() -> listUserTransactionUseCase.execute(userId)).isEmpty());
    }
}
