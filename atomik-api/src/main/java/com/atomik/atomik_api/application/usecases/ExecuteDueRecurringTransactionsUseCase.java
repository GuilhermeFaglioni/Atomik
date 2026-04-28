package com.atomik.atomik_api.application.usecases;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.RecurringScheduleService;
import com.atomik.atomik_api.application.service.TransactionAuditService;
import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class ExecuteDueRecurringTransactionsUseCase {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionReconciliationService transactionReconciliationService;
    private final TransactionAuditService transactionAuditService;
    private final RecurringScheduleService recurringScheduleService;

    public ExecuteDueRecurringTransactionsUseCase(RecurringTransactionRepository recurringTransactionRepository,
            TransactionRepository transactionRepository,
            TransactionReconciliationService transactionReconciliationService,
            TransactionAuditService transactionAuditService,
            RecurringScheduleService recurringScheduleService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.transactionReconciliationService = transactionReconciliationService;
        this.transactionAuditService = transactionAuditService;
        this.recurringScheduleService = recurringScheduleService;
    }

    @Transactional
    public int execute() {
        return execute(LocalDateTime.now());
    }

    @Transactional
    public int execute(LocalDateTime executionTime) {
        int createdTransactions = 0;

        for (RecurringTransaction recurringTransaction : recurringTransactionRepository.findDueTransactions(executionTime)) {
            RecurringTransaction currentRecurring = recurringTransaction;

            while (currentRecurring.getStatus() == RecurringStatus.ACTIVE
                    && !currentRecurring.getNextDueDate().isAfter(executionTime)) {
                Transaction transaction = toTransaction(currentRecurring);
                transactionReconciliationService.apply(transaction);
                transactionAuditService.logCreated(transaction, "Recurring Transaction Execution");
                transactionRepository.save(transaction);
                createdTransactions++;

                LocalDateTime nextDueDate = recurringScheduleService.nextDueDate(currentRecurring.getNextDueDate(),
                        currentRecurring.getFrequency());
                RecurringStatus nextStatus = shouldComplete(currentRecurring, nextDueDate)
                        ? RecurringStatus.COMPLETED
                        : RecurringStatus.ACTIVE;
                currentRecurring = currentRecurring.reschedule(nextDueDate, nextStatus);
            }

            recurringTransactionRepository.save(currentRecurring);
        }

        return createdTransactions;
    }

    private Transaction toTransaction(RecurringTransaction recurringTransaction) {
        if (recurringTransaction.getType() == TransactionType.TRANSFER) {
            return Transaction.createTransfer(recurringTransaction.getUserId(), recurringTransaction.getCategoryId(),
                    recurringTransaction.getSourceAccountId(), recurringTransaction.getDestinationAccountId(),
                    recurringTransaction.getAmount(), recurringTransaction.getDescription(),
                    recurringTransaction.getNextDueDate());
        }

        return Transaction.createSingleEntry(recurringTransaction.getUserId(), recurringTransaction.getCategoryId(),
                recurringTransaction.getSourceAccountId(), recurringTransaction.getAmount(),
                recurringTransaction.getDescription(), recurringTransaction.getNextDueDate(),
                recurringTransaction.getType());
    }

    private boolean shouldComplete(RecurringTransaction recurringTransaction, LocalDateTime nextDueDate) {
        return recurringTransaction.getEndDate() != null && nextDueDate.isAfter(recurringTransaction.getEndDate());
    }
}
