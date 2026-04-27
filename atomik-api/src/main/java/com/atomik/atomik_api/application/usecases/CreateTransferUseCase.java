package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.service.TransactionAuditService;
import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class CreateTransferUseCase {
    private final TransactionRepository transactionRepository;
    private final FinancialResourceOwnershipService financialResourceOwnershipService;
    private final TransactionReconciliationService transactionReconciliationService;
    private final TransactionAuditService transactionAuditService;

    public CreateTransferUseCase(TransactionRepository transactionRepository,
            FinancialResourceOwnershipService financialResourceOwnershipService,
            TransactionReconciliationService transactionReconciliationService,
            TransactionAuditService transactionAuditService) {
        this.transactionRepository = transactionRepository;
        this.financialResourceOwnershipService = financialResourceOwnershipService;
        this.transactionReconciliationService = transactionReconciliationService;
        this.transactionAuditService = transactionAuditService;
    }

    @Transactional
    public TransactionCreatedResponse execute(String userId, String categoryId, String sourceAccountId,
            String destinationAccountId, BigDecimal amount, String description, LocalDateTime date) {
        UUID parsedUserId = financialResourceOwnershipService.requireExistingUser(userId);
        financialResourceOwnershipService.requireOwnedCategory(parsedUserId, categoryId);
        Account sourceAccount = financialResourceOwnershipService.requireOwnedAccount(parsedUserId, sourceAccountId,
                "Source account not found");
        Account destinationAccount = financialResourceOwnershipService.requireOwnedAccount(parsedUserId,
                destinationAccountId, "Destination account not found");

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        var transaction = Transaction.createTransfer(parsedUserId, UUID.fromString(categoryId),
                UUID.fromString(sourceAccountId), UUID.fromString(destinationAccountId), amount, description, date);

        transactionReconciliationService.apply(transaction);
        transactionAuditService.logCreated(transaction, "newTransfer");
        transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    private TransactionCreatedResponse toResponse(Transaction transaction) {
        return new TransactionCreatedResponse(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), "Transfer created successfully");
    }

}
