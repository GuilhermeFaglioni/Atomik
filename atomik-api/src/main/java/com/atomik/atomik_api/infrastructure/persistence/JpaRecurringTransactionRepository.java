package com.atomik.atomik_api.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atomik.atomik_api.domain.model.RecurringStatus;

@Repository
public interface JpaRecurringTransactionRepository extends JpaRepository<RecurringTransactionEntity, UUID> {
    List<RecurringTransactionEntity> findByUserId(UUID userId);

    List<RecurringTransactionEntity> findByUserIdAndStatus(UUID userId, RecurringStatus status);

    List<RecurringTransactionEntity> findByUserIdAndStatusAndNextDueDateBefore(
            UUID userId,
            RecurringStatus status,
            LocalDateTime date);
}
