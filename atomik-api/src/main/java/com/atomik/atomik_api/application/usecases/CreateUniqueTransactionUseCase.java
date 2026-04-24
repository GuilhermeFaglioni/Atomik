package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class CreateUniqueTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final FinancialResourceOwnershipService financialResourceOwnershipService;
    private final TransactionReconciliationService transactionReconciliationService;

    public CreateUniqueTransactionUseCase(TransactionRepository transactionRepository,
            AuditLogRepository auditLogRepository,
            FinancialResourceOwnershipService financialResourceOwnershipService,
            TransactionReconciliationService transactionReconciliationService) {
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.financialResourceOwnershipService = financialResourceOwnershipService;
        this.transactionReconciliationService = transactionReconciliationService;
    }

    @Transactional
    public TransactionCreatedResponse execute(String userId,
            String categoryId,
            String accountId,
            BigDecimal amount,
            String description,
            LocalDateTime date,
            TransactionType type) {
        UUID parsedUserId = financialResourceOwnershipService.requireExistingUser(userId);
        financialResourceOwnershipService.requireOwnedCategory(parsedUserId, categoryId);
        financialResourceOwnershipService.requireOwnedAccount(parsedUserId, accountId, "Account not found");

        if (type.equals(TransactionType.TRANSFER)) {
            throw new IllegalArgumentException("Use createTransfer for transfer type");
        }
        var transaction = Transaction.createSingleEntry(parsedUserId, UUID.fromString(categoryId),
                UUID.fromString(accountId), amount, description, date, type);

        transactionReconciliationService.apply(transaction);
        AuditLog auditLog = AuditLog.createNewAuditLog(transaction.getId(), "New Unique Transaction", "N/A",
                transaction.getAmount().toString());
        auditLogRepository.save(auditLog);
        transactionRepository.save(transaction);
        return toResponse(transaction);

    }

    private TransactionCreatedResponse toResponse(Transaction transaction) {
        return new TransactionCreatedResponse(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), "Transaction created successfully");
    }

}
