package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.AuditLog;

@Component
public class AuditLogMapper {
    public AuditLog toDomain(AuditLogEntity entity) {
        if (entity == null) return null;
        return new AuditLog(
                entity.getId(),
                entity.getTransactionId(),
                entity.getFieldChanged(),
                entity.getOldValue(),
                entity.getNewValue(),
                entity.getChangedAt());
    }

    public AuditLogEntity toEntity(AuditLog domain) {
        if (domain == null) return null;
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.getId());
        entity.setFieldChanged(domain.getFieldChanged());
        entity.setOldValue(domain.getOldValue());
        entity.setNewValue(domain.getNewValue());
        entity.setChangedAt(domain.getChangedAt());
        // Relationship handled by RepositoryAdapter
        return entity;
    }
}
