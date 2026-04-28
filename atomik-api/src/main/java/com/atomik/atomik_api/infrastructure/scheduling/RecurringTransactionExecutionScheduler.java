package com.atomik.atomik_api.infrastructure.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.atomik.atomik_api.application.usecases.ExecuteDueRecurringTransactionsUseCase;

@Component
public class RecurringTransactionExecutionScheduler {
    private final ExecuteDueRecurringTransactionsUseCase executeDueRecurringTransactionsUseCase;

    public RecurringTransactionExecutionScheduler(
            ExecuteDueRecurringTransactionsUseCase executeDueRecurringTransactionsUseCase) {
        this.executeDueRecurringTransactionsUseCase = executeDueRecurringTransactionsUseCase;
    }

    @Scheduled(fixedDelayString = "${atomik.recurring.execution-delay-ms:60000}")
    public void executeDueTransactions() {
        executeDueRecurringTransactionsUseCase.execute();
    }
}
