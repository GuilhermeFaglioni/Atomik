package com.atomik.atomik_api.infrastructure.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.infrastructure.persistence.AuditLogMapper;
import com.atomik.atomik_api.infrastructure.persistence.JpaAuditLogRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaTransactionRepository;

@Component
public class DatabaseAuditLogAdapter implements AuditLogRepository {
    private final JpaAuditLogRepository jpaAuditLogRepository;
    private final JpaTransactionRepository jpaTransactionRepository;
    private final AuditLogMapper auditLogMapper;

    public DatabaseAuditLogAdapter(
            JpaAuditLogRepository jpaAuditLogRepository,
            JpaTransactionRepository jpaTransactionRepository,
            AuditLogMapper auditLogMapper) {
        this.jpaAuditLogRepository = jpaAuditLogRepository;
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public void save(AuditLog auditLog) {
        var transactionRef = jpaTransactionRepository.getReferenceById(auditLog.getTransactionId());

        var entity = auditLogMapper.toEntity(auditLog);
        entity.setTransaction(transactionRef);

        jpaAuditLogRepository.save(entity);
    }

    @Override
    public List<AuditLog> findByUserId(String userId) {
        return jpaAuditLogRepository.findByTransaction_User_Id(UUID.fromString(userId))
                .stream()
                .map(auditLogMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate) {
        var start = startDate.atStartOfDay();
        var end = endDate.atTime(23, 59, 59);

        return jpaAuditLogRepository.findByTransaction_User_IdAndChangedAtBetween(UUID.fromString(userId), start, end)
                .stream()
                .map(auditLogMapper::toDomain)
                .toList();
    }

    @Override
    public AuditLog findById(String id) {
        return jpaAuditLogRepository.findById(UUID.fromString(id))
                .map(auditLogMapper::toDomain)
                .orElse(null);
    }

}
