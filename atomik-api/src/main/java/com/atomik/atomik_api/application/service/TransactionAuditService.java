package com.atomik.atomik_api.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;

@Service
public class TransactionAuditService {
    private final AuditLogRepository auditLogRepository;

    public TransactionAuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logCreated(Transaction transaction, String fieldChanged) {
        auditLogRepository.save(AuditLog.createNewAuditLog(
                transaction.getId(),
                fieldChanged,
                "N/A",
                transaction.getAmount().toString()));
    }

    public void logDeleted(Transaction transaction) {
        auditLogRepository.save(AuditLog.createNewAuditLog(
                transaction.getId(),
                "Deleted Transaction",
                "N/A",
                transaction.getAmount().toString()));
    }

    public void logChanges(Transaction oldState, Transaction newState) {
        UUID entityId = newState.getId();
        logIfChanged(entityId, "category_id", oldState.getCategoryId(), newState.getCategoryId());
        logIfChanged(entityId, "source_account_id", oldState.getSourceAccountId(), newState.getSourceAccountId());
        logIfChanged(entityId, "destination_account_id", oldState.getDestinationAccountId(),
                newState.getDestinationAccountId());
        logIfChanged(entityId, "amount", oldState.getAmount(), newState.getAmount());
        logIfChanged(entityId, "description", oldState.getDescription(), newState.getDescription());
        logIfChanged(entityId, "date", oldState.getDate(), newState.getDate());
        logIfChanged(entityId, "type", oldState.getType(), newState.getType());
    }

    private void logIfChanged(UUID entityId, String field, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            auditLogRepository.save(AuditLog.createNewAuditLog(
                    entityId,
                    field,
                    oldValue != null ? oldValue.toString() : "N/A",
                    newValue != null ? newValue.toString() : "N/A"));
        }
    }
}
