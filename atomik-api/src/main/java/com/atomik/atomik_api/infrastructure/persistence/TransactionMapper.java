package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Transaction;

@Component
public class TransactionMapper {
    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null)
            return null;
        return new Transaction(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getDate(),
                entity.getType(),
                entity.getSyncStatus(),
                entity.getCreatedAt());
    }

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null)
            return null;
        TransactionEntity entity = new TransactionEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setDescription(domain.getDescription());
        entity.setDate(domain.getDate());
        entity.setType(domain.getType());
        entity.setSyncStatus(domain.getSyncStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
