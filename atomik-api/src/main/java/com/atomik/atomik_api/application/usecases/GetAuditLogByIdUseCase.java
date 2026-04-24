package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AuditLogResponseDTO;
import com.atomik.atomik_api.domain.exception.AuditLogNotFoundException;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;

@Service
public class GetAuditLogByIdUseCase {
    private final AuditLogRepository auditLogRepository;
    private final TransactionRepository transactionRepository;

    public GetAuditLogByIdUseCase(AuditLogRepository auditLogRepository, TransactionRepository transactionRepository) {
        this.auditLogRepository = auditLogRepository;
        this.transactionRepository = transactionRepository;
    }

    public AuditLogResponseDTO execute(String id, String userId) {
        AuditLog auditLog = auditLogRepository.findById(id);
        if (auditLog == null) {
            throw new AuditLogNotFoundException("Audit Log not found");
        }

        var transaction = transactionRepository.findById(auditLog.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (!transaction.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("You do not have permission to access this resource");
        }

        return toResponseDTO(auditLog);
    }

    private AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {
        return new AuditLogResponseDTO(
                auditLog.getId().toString(),
                auditLog.getTransactionId().toString(),
                auditLog.getFieldChanged(),
                auditLog.getOldValue(),
                auditLog.getNewValue(),
                auditLog.getChangedAt());
    }
}
