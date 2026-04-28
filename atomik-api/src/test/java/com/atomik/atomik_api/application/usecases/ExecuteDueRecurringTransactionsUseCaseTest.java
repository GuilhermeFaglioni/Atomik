package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.application.service.RecurringScheduleService;
import com.atomik.atomik_api.application.service.TransactionAuditService;
import com.atomik.atomik_api.domain.model.RecurringFrequency;
import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@ExtendWith(MockitoExtension.class)
class ExecuteDueRecurringTransactionsUseCaseTest {

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionReconciliationService transactionReconciliationService;

    @Mock
    private TransactionAuditService transactionAuditService;

    @Mock
    private RecurringScheduleService recurringScheduleService;

    @InjectMocks
    private ExecuteDueRecurringTransactionsUseCase executeDueRecurringTransactionsUseCase;

    @Test
    @DisplayName("Should execute due recurring transaction and reschedule it")
    void shouldExecuteDueRecurringTransactionAndRescheduleIt() {
        LocalDateTime now = LocalDateTime.parse("2026-04-27T10:00:00");
        LocalDateTime nextDueDate = now.minusDays(1);
        LocalDateTime rescheduledDate = nextDueDate.plusMonths(1);
        RecurringTransaction recurringTransaction = recurring(nextDueDate, null, RecurringFrequency.MONTHLY);

        when(recurringTransactionRepository.findDueTransactions(now)).thenReturn(List.of(recurringTransaction));
        when(recurringScheduleService.nextDueDate(nextDueDate, RecurringFrequency.MONTHLY)).thenReturn(rescheduledDate);

        int createdTransactions = executeDueRecurringTransactionsUseCase.execute(now);

        assertEquals(1, createdTransactions);
        verify(transactionReconciliationService).apply(any(Transaction.class));
        verify(transactionAuditService).logCreated(any(Transaction.class), any());
        verify(transactionRepository).save(any(Transaction.class));
        verify(recurringTransactionRepository).save(any(RecurringTransaction.class));
    }

    @Test
    @DisplayName("Should catch up multiple overdue executions and complete recurring transaction after end date")
    void shouldCatchUpMultipleOverdueExecutionsAndCompleteRecurringTransactionAfterEndDate() {
        LocalDateTime executionTime = LocalDateTime.parse("2026-04-27T10:00:00");
        LocalDateTime firstDueDate = LocalDateTime.parse("2026-04-25T10:00:00");
        LocalDateTime secondDueDate = LocalDateTime.parse("2026-04-26T10:00:00");
        LocalDateTime thirdDueDate = LocalDateTime.parse("2026-04-27T10:00:00");
        LocalDateTime afterEndDate = LocalDateTime.parse("2026-04-28T10:00:00");
        RecurringTransaction recurringTransaction = recurring(firstDueDate, thirdDueDate, RecurringFrequency.DAILY);

        when(recurringTransactionRepository.findDueTransactions(executionTime)).thenReturn(List.of(recurringTransaction));
        when(recurringScheduleService.nextDueDate(firstDueDate, RecurringFrequency.DAILY)).thenReturn(secondDueDate);
        when(recurringScheduleService.nextDueDate(secondDueDate, RecurringFrequency.DAILY)).thenReturn(thirdDueDate);
        when(recurringScheduleService.nextDueDate(thirdDueDate, RecurringFrequency.DAILY)).thenReturn(afterEndDate);

        int createdTransactions = executeDueRecurringTransactionsUseCase.execute(executionTime);

        assertEquals(3, createdTransactions);
        verify(transactionRepository, times(3)).save(any(Transaction.class));
        verify(recurringTransactionRepository).save(any(RecurringTransaction.class));
    }

    private RecurringTransaction recurring(LocalDateTime nextDueDate, LocalDateTime endDate,
            RecurringFrequency frequency) {
        return new RecurringTransaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                null, BigDecimal.TEN, "Recurring", TransactionType.EXPENSE,
                nextDueDate.minusDays(1), endDate, nextDueDate, frequency, RecurringStatus.ACTIVE,
                LocalDateTime.parse("2026-04-20T10:00:00"));
    }
}
