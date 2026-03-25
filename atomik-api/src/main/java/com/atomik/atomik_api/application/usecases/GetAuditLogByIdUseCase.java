package com.atomik.atomik_api.application.usecases;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AuditLogResponseDTO;
import com.atomik.atomik_api.domain.exception.AuditLogNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;

@Service
public class GetAuditLogByIdUseCase {
    private final AuditLogRepository auditLogRepository;

    public GetAuditLogByIdUseCase(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogResponseDTO execute(String id) {
        AuditLog auditLog = auditLogRepository.findById(id);
        if (auditLog == null) {
            throw new AuditLogNotFoundException("Audit Log not found");
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
