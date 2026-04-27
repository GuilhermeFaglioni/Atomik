package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.service.TransactionAuditService;
import com.atomik.atomik_api.application.dto.TransactionResponseDTO;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class UpdateTransactionUseCase {
        private final TransactionRepository transactionRepository;
        private final TransactionReconciliationService transactionReconciliationService;
        private final FinancialResourceOwnershipService financialResourceOwnershipService;
        private final TransactionAuditService transactionAuditService;

        public UpdateTransactionUseCase(TransactionRepository transactionRepository,
                        TransactionReconciliationService transactionReconciliationService,
                        FinancialResourceOwnershipService financialResourceOwnershipService,
                        TransactionAuditService transactionAuditService) {
                this.transactionRepository = transactionRepository;
                this.transactionReconciliationService = transactionReconciliationService;
                this.financialResourceOwnershipService = financialResourceOwnershipService;
                this.transactionAuditService = transactionAuditService;
        }

        @Transactional
        public TransactionResponseDTO execute(String transactionId, String userId, String categoryId,
                        String sourceAccountId,
                        String destinationAccountId,
                        BigDecimal amount, String description, LocalDateTime date, TransactionType type) {
                Transaction sourceTransaction = transactionRepository.findById(UUID.fromString(transactionId))
                                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
                UUID parsedUserId = financialResourceOwnershipService.requireExistingUser(userId);
                if (!parsedUserId.equals(sourceTransaction.getUserId())) {
                        throw new UnauthorizedException("User not authorized to access this transaction");
                }
                financialResourceOwnershipService.requireOwnedCategory(parsedUserId, categoryId);
                financialResourceOwnershipService.requireOwnedAccount(parsedUserId, sourceAccountId,
                                "Source account not found");
                if (type == TransactionType.TRANSFER) {
                        financialResourceOwnershipService.requireOwnedAccount(parsedUserId, destinationAccountId,
                                        "Destination account not found");
                }

                var updatedTransaction = new Transaction(sourceTransaction.getId(), sourceTransaction.getUserId(),
                                UUID.fromString(categoryId), UUID.fromString(sourceAccountId),
                                type == TransactionType.TRANSFER && destinationAccountId != null
                                                ? UUID.fromString(destinationAccountId)
                                                : null,
                                amount, description, date, type, sourceTransaction.getSyncStatus(),
                                sourceTransaction.getCreatedAt());
                updatedTransaction.validate();

                transactionReconciliationService.reconcile(sourceTransaction, updatedTransaction);

                var savedTransaction = transactionRepository.update(updatedTransaction)
                                .orElseThrow(() -> new RuntimeException("Error updating transaction"));

                transactionAuditService.logChanges(sourceTransaction, updatedTransaction);

                return toResponse(savedTransaction);
        }

        private TransactionResponseDTO toResponse(Transaction transaction) {
                return new TransactionResponseDTO(transaction.getId().toString(), transaction.getType().toString(),
                                transaction.getAmount(), transaction.getDescription(),
                                transaction.getDate().toString());
        }
}
