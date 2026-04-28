package com.atomik.atomik_api.infrastructure.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaRecurringTransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.RecurringTransactionMapper;

@Component
public class DatabaseRecurringTransactionRepositoryAdapter implements RecurringTransactionRepository {
    private final JpaRecurringTransactionRepository jpaRecurringTransactionRepository;
    private final RecurringTransactionMapper recurringTransactionMapper;

    public DatabaseRecurringTransactionRepositoryAdapter(
            JpaRecurringTransactionRepository jpaRecurringTransactionRepository,
            RecurringTransactionMapper recurringTransactionMapper) {
        this.jpaRecurringTransactionRepository = jpaRecurringTransactionRepository;
        this.recurringTransactionMapper = recurringTransactionMapper;
    }

    @Override
    public Optional<RecurringTransaction> findById(UUID id) {
        return jpaRecurringTransactionRepository.findById(id).map(recurringTransactionMapper::toDomain);
    }

    @Override
    public List<RecurringTransaction> findByUserId(UUID userId) {
        return jpaRecurringTransactionRepository.findByUser_Id(userId).stream()
                .map(recurringTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<RecurringTransaction> findActiveByUserId(UUID userId) {
        return jpaRecurringTransactionRepository.findByUser_IdAndStatus(userId, RecurringStatus.ACTIVE).stream()
                .map(recurringTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<RecurringTransaction> findActiveByUserIdAndNextDueDateBefore(UUID userId, LocalDateTime date) {
        return jpaRecurringTransactionRepository
                .findByUser_IdAndStatusAndNextDueDateBefore(userId, RecurringStatus.ACTIVE, date).stream()
                .map(recurringTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<RecurringTransaction> findDueTransactions(LocalDateTime date) {
        return jpaRecurringTransactionRepository.findByStatusAndNextDueDateBefore(RecurringStatus.ACTIVE, date).stream()
                .map(recurringTransactionMapper::toDomain)
                .toList();
    }

    @Override
    public void save(RecurringTransaction recurringTransaction) {
        jpaRecurringTransactionRepository.save(recurringTransactionMapper.toEntity(recurringTransaction));
    }

    @Override
    public void delete(UUID id) {
        jpaRecurringTransactionRepository.deleteById(id);
    }

}
