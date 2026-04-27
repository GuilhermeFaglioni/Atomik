package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AuditLogResponseDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class ListAllUserAuditLogs {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public ListAllUserAuditLogs(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogResponseDTO> execute(String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var auditLogs = auditLogRepository.findByUserId(userId);

        return auditLogs.stream().map(this::toResponseDTO).toList();
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
