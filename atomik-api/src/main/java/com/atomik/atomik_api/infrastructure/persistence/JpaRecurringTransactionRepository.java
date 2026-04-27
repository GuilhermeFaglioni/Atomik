package com.atomik.atomik_api.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atomik.atomik_api.domain.model.RecurringStatus;

@Repository
public interface JpaRecurringTransactionRepository extends JpaRepository<RecurringTransactionEntity, UUID> {
    List<RecurringTransactionEntity> findByUser_Id(UUID userId);

    List<RecurringTransactionEntity> findByUser_IdAndStatus(UUID userId, RecurringStatus status);

    List<RecurringTransactionEntity> findByUser_IdAndStatusAndNextDueDateBefore(
            UUID userId,
            RecurringStatus status,
            LocalDateTime date);
}
