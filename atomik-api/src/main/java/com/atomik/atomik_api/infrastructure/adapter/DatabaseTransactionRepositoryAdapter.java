package com.atomik.atomik_api.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaTransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.TransactionMapper;

@Component
public class DatabaseTransactionRepositoryAdapter implements TransactionRepository {
    private final JpaTransactionRepository jpaTransactionRepository;
    private final TransactionMapper transactionMapper;

    public DatabaseTransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository,
            TransactionMapper transactionMapper) {
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void save(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        jpaTransactionRepository.save(entity);
    }

    @Override
    public void delete(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        jpaTransactionRepository.delete(entity);
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaTransactionRepository.findById(id).map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return jpaTransactionRepository.findBySourceAccount_Id(accountId).stream().map(transactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return jpaTransactionRepository.findByUser_Id(userId).stream().map(transactionMapper::toDomain).toList();
    }
}
