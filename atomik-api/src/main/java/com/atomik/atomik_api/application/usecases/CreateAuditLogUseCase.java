package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateAuditLogUseCase {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final TransactionRepository transactionRepository;

    public CreateAuditLogUseCase(UserRepository userRepository, AuditLogRepository auditLogRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.transactionRepository = transactionRepository;
    }

    public void execute(String userId, String transactionId, String fieldChanged, String oldValue, String newValue) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        var auditLog = AuditLog.createNewAuditLog(UUID.fromString(transactionId), fieldChanged, oldValue, newValue);
        auditLogRepository.save(auditLog);
    }
}
