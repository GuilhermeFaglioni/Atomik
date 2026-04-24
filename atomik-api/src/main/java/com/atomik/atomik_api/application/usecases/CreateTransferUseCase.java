package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class CreateTransferUseCase {
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final FinancialResourceOwnershipService financialResourceOwnershipService;
    private final TransactionReconciliationService transactionReconciliationService;

    public CreateTransferUseCase(TransactionRepository transactionRepository, AuditLogRepository auditLogRepository,
            FinancialResourceOwnershipService financialResourceOwnershipService,
            TransactionReconciliationService transactionReconciliationService) {
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.financialResourceOwnershipService = financialResourceOwnershipService;
        this.transactionReconciliationService = transactionReconciliationService;
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

        AuditLog auditLog = AuditLog.createNewAuditLog(transaction.getId(), "newTransfer", "N/A",
                transaction.getAmount().toString());

        transactionReconciliationService.apply(transaction);
        auditLogRepository.save(auditLog);
        transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    private TransactionCreatedResponse toResponse(Transaction transaction) {
        return new TransactionCreatedResponse(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), "Transfer created successfully");
    }

}
