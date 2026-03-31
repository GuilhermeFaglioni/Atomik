package com.atomik.atomik_api.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.RecurringTransaction;

public interface RecurringTransactionRepository {
    Optional<RecurringTransaction> findById(UUID id);

    List<RecurringTransaction> findByUserId(UUID userId);

    List<RecurringTransaction> findActiveByUserId(UUID userId);

    List<RecurringTransaction> findActiveByUserIdAndNextDueDateBefore(UUID userId, LocalDateTime date);

    void save(RecurringTransaction recurringTransaction);

    void delete(UUID id);
}
