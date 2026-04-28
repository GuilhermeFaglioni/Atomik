package com.atomik.atomik_api.infrastructure.scheduling;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.application.usecases.ExecuteDueRecurringTransactionsUseCase;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionExecutionSchedulerTest {

    @Mock
    private ExecuteDueRecurringTransactionsUseCase executeDueRecurringTransactionsUseCase;

    @InjectMocks
    private RecurringTransactionExecutionScheduler recurringTransactionExecutionScheduler;

    @Test
    @DisplayName("Should delegate due recurring execution to use case")
    void shouldDelegateDueRecurringExecutionToUseCase() {
        recurringTransactionExecutionScheduler.executeDueTransactions();

        verify(executeDueRecurringTransactionsUseCase).execute();
    }
}
