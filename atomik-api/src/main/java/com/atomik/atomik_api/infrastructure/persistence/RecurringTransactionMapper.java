package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.RecurringTransaction;

@Component
public class RecurringTransactionMapper {
    public RecurringTransactionEntity toEntity(RecurringTransaction recurringTransaction) {
        RecurringTransactionEntity entity = new RecurringTransactionEntity();
        entity.setId(recurringTransaction.getId());
        if (recurringTransaction.getUserId() != null) {
            UserEntity user = new UserEntity();
            user.setId(recurringTransaction.getUserId());
            entity.setUser(user);
        }
        if (recurringTransaction.getCategoryId() != null) {
            CategoryEntity category = new CategoryEntity();
            category.setId(recurringTransaction.getCategoryId());
            entity.setCategory(category);
        }
        if (recurringTransaction.getSourceAccountId() != null) {
            AccountEntity sourceAccount = new AccountEntity();
            sourceAccount.setId(recurringTransaction.getSourceAccountId());
            entity.setSourceAccount(sourceAccount);
        }
        if (recurringTransaction.getDestinationAccountId() != null) {
            AccountEntity destinationAccount = new AccountEntity();
            destinationAccount.setId(recurringTransaction.getDestinationAccountId());
            entity.setDestinationAccount(destinationAccount);
        }
        entity.setAmount(recurringTransaction.getAmount());
        entity.setDescription(recurringTransaction.getDescription());
        entity.setType(recurringTransaction.getType());
        entity.setStartDate(recurringTransaction.getStartDate());
        entity.setEndDate(recurringTransaction.getEndDate());
        entity.setNextDueDate(recurringTransaction.getNextDueDate());
        entity.setFrequency(recurringTransaction.getFrequency());
        entity.setStatus(recurringTransaction.getStatus());
        entity.setCreatedAt(recurringTransaction.getCreatedAt());
        return entity;
    }

    public RecurringTransaction toDomain(RecurringTransactionEntity entity) {
        return new RecurringTransaction(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getType(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getNextDueDate(),
                entity.getFrequency(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
