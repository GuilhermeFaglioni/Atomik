package com.atomik.atomik_api.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.AccountEntity;
import com.atomik.atomik_api.infrastructure.persistence.CategoryEntity;
import com.atomik.atomik_api.infrastructure.persistence.JpaAccountRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaCategoryRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaTransactionRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;
import com.atomik.atomik_api.infrastructure.persistence.TransactionMapper;
import com.atomik.atomik_api.infrastructure.persistence.UserEntity;

@Component
public class DatabaseTransactionRepositoryAdapter implements TransactionRepository {
    private final JpaTransactionRepository jpaTransactionRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaAccountRepository jpaAccountRepository;
    private final TransactionMapper transactionMapper;

    public DatabaseTransactionRepositoryAdapter(JpaTransactionRepository jpaTransactionRepository,
            JpaUserRepository jpaUserRepository, JpaCategoryRepository jpaCategoryRepository,
            JpaAccountRepository jpaAccountRepository, TransactionMapper transactionMapper) {
        this.jpaTransactionRepository = jpaTransactionRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaAccountRepository = jpaAccountRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void save(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction,
                getUserReference(transaction.getUserId()),
                getCategoryReference(transaction.getCategoryId()),
                getAccountReference(transaction.getSourceAccountId()),
                getAccountReference(transaction.getDestinationAccountId()));
        jpaTransactionRepository.save(entity);
    }

    @Override
    public void delete(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        jpaTransactionRepository.delete(entity);
    }

    @Override
    public Optional<Transaction> update(Transaction transaction) {
        return jpaTransactionRepository.findById(transaction.getId()).map(existingEntity -> {
            existingEntity.setUser(getUserReference(transaction.getUserId()));
            existingEntity.setCategory(getCategoryReference(transaction.getCategoryId()));
            existingEntity.setSourceAccount(getAccountReference(transaction.getSourceAccountId()));
            existingEntity.setDestinationAccount(getAccountReference(transaction.getDestinationAccountId()));
            existingEntity.setAmount(transaction.getAmount());
            existingEntity.setDescription(transaction.getDescription());
            existingEntity.setDate(transaction.getDate());
            existingEntity.setType(transaction.getType());
            existingEntity.setSyncStatus(transaction.getSyncStatus());
            existingEntity.setCreatedAt(transaction.getCreatedAt());
            return transactionMapper.toDomain(jpaTransactionRepository.save(existingEntity));
        });
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

    private UserEntity getUserReference(UUID userId) {
        return jpaUserRepository.getReferenceById(userId);
    }

    private CategoryEntity getCategoryReference(UUID categoryId) {
        return jpaCategoryRepository.getReferenceById(categoryId);
    }

    private AccountEntity getAccountReference(UUID accountId) {
        if (accountId == null) {
            return null;
        }
        return jpaAccountRepository.getReferenceById(accountId);
    }
}
