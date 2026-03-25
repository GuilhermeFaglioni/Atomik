package com.atomik.atomik_api.domain.repository;

import java.time.LocalDate;
import java.util.List;

import com.atomik.atomik_api.domain.model.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    AuditLog findById(String id);
}
