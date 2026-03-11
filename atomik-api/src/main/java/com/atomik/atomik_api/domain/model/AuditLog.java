package com.atomik.atomik_api.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;

import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditLog {
    private final UUID id;
    private final UUID transactionId;
    private final String fieldChanged;
    private final String oldValue;
    private final String newValue;
    private final LocalDateTime changedAt;

    public static AuditLog createNewAuditLog(UUID transactionId, String fieldChanged, String oldValue,
            String newValue) {
        AuditLog auditLog = new AuditLog(
                UUID.randomUUID(),
                transactionId,
                fieldChanged,
                oldValue,
                newValue,
                LocalDateTime.now());
        auditLog.validate();
        return auditLog;
    }

    public void validate() {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        if (fieldChanged == null || fieldChanged.isBlank()) {
            throw new IllegalArgumentException("Field changed is required");
        }
        if (oldValue == null || oldValue.isBlank()) {
            throw new IllegalArgumentException("Old value is required");
        }
        if (newValue == null || newValue.isBlank()) {
            throw new IllegalArgumentException("New value is required");
        }
        if (changedAt == null) {
            throw new IllegalArgumentException("Changed at is required");
        }
    }
}
