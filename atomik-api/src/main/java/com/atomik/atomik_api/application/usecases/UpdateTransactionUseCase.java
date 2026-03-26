package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.TransactionResponseDTO;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class UpdateTransactionUseCase {
        private final TransactionRepository transactionRepository;
        private final UserRepository userRepository;
        private final AuditLogRepository auditLogRepository;
        private final TransactionReconciliationService transactionReconciliationService;

        public UpdateTransactionUseCase(TransactionRepository transactionRepository, UserRepository userRepository,
                        AuditLogRepository auditLogRepository,
                        TransactionReconciliationService transactionReconciliationService) {
                this.transactionRepository = transactionRepository;
                this.userRepository = userRepository;
                this.auditLogRepository = auditLogRepository;
                this.transactionReconciliationService = transactionReconciliationService;
        }

        @Transactional
        public TransactionResponseDTO execute(String transactionId, String userId, String categoryId,
                        String sourceAccountId,
                        String destinationAccountId,
                        BigDecimal amount, String description, LocalDateTime date, TransactionType type) {
                Transaction sourceTransaction = transactionRepository.findById(UUID.fromString(transactionId))
                                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
                User user = userRepository.findById(UUID.fromString(userId))
                                .orElseThrow(() -> new UserNotFoundException("User not found"));
                if (!user.getId().equals(sourceTransaction.getUserId())) {
                        throw new UnauthorizedException("User not authorized to access this transaction");
                }
                var updatedTransaction = new Transaction(sourceTransaction.getId(), sourceTransaction.getUserId(),
                                UUID.fromString(categoryId), UUID.fromString(sourceAccountId),
                                UUID.fromString(destinationAccountId),
                                amount, description, date, type, sourceTransaction.getSyncStatus(),
                                sourceTransaction.getCreatedAt());
                updatedTransaction.validate();

                transactionReconciliationService.reconcile(sourceTransaction, updatedTransaction);

                var savedTransaction = transactionRepository.update(updatedTransaction)
                                .orElseThrow(() -> new RuntimeException("Error updating transaction"));

                generateAuditLogs(sourceTransaction, updatedTransaction);

                return toResponse(savedTransaction);
        }

        private TransactionResponseDTO toResponse(Transaction transaction) {
                return new TransactionResponseDTO(transaction.getId().toString(), transaction.getType().toString(),
                                transaction.getAmount(), transaction.getDescription(),
                                transaction.getDate().toString());
        }

        private void generateAuditLogs(Transaction oldS, Transaction newS) {
                UUID entityId = newS.getId();
                logIfChanged(entityId, "category_id", oldS.getCategoryId(), newS.getCategoryId());
                logIfChanged(entityId, "source_account_id", oldS.getSourceAccountId(), newS.getSourceAccountId());
                logIfChanged(entityId, "destination_account_id", oldS.getDestinationAccountId(),
                                newS.getDestinationAccountId());
                logIfChanged(entityId, "amount", oldS.getAmount(), newS.getAmount());
                logIfChanged(entityId, "description", oldS.getDescription(), newS.getDescription());
                logIfChanged(entityId, "date", oldS.getDate(), newS.getDate());
                logIfChanged(entityId, "type", oldS.getType(), newS.getType());
        }

        private void logIfChanged(UUID entityId, String field, Object oldVal, Object newVal) {
                if (!Objects.equals(oldVal, newVal)) {
                        auditLogRepository.save(AuditLog.createNewAuditLog(
                                        entityId,
                                        field,
                                        oldVal != null ? oldVal.toString() : "N/A",
                                        newVal != null ? newVal.toString() : "N/A"));
                }
        }
}
