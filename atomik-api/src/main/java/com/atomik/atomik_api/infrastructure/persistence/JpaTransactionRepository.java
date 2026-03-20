package com.atomik.atomik_api.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findBySourceAccount_Id(UUID accountId);

    List<TransactionEntity> findByUser_Id(UUID userId);
}
