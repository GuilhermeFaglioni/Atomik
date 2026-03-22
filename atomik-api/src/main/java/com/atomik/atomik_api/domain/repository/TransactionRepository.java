package com.atomik.atomik_api.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction);

    void delete(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByAccountId(UUID accountId);

    List<Transaction> findByUserId(UUID userId);

    Optional<Transaction> update(Transaction transaction);

}
