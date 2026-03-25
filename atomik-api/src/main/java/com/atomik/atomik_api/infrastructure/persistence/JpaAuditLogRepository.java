package com.atomik.atomik_api.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    List<AuditLogEntity> findByTransaction_User_Id(UUID userId);

    List<AuditLogEntity> findByTransaction_User_IdAndChangedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);
}
