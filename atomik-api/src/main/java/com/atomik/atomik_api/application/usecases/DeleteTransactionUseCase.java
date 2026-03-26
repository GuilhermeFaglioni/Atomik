package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.DeleteTransactionDTO;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionReconciliationService transactionReconciliationService;
    private final AuditLogRepository auditLogRepository;

    public DeleteTransactionUseCase(TransactionRepository transactionRepository, UserRepository userRepository,
            TransactionReconciliationService transactionReconciliationService, AuditLogRepository auditLogRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.transactionReconciliationService = transactionReconciliationService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public DeleteTransactionDTO execute(String userId, String transactionId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (!transaction.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("User not authorized to delete this transaction");
        }

        transactionReconciliationService.rollBack(transaction);

        AuditLog auditLog = AuditLog.createNewAuditLog(transaction.getId(), "Deleted Transaction", "N/A",
                transaction.getAmount().toString());
        auditLogRepository.save(auditLog);

        transactionRepository.delete(transaction);

        return new DeleteTransactionDTO(transactionId, "Transaction deleted successfully");
    }
}
