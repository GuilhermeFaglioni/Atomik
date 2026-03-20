package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Budget;

@Component
public class BudgetMapper {
    public Budget toDomain(BudgetEntity entity) {
        if (entity == null)
            return null;
        return new Budget(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getLimitAmount(),
                entity.getMonth(),
                entity.getYear());
    }

    public BudgetEntity toEntity(Budget domain) {
        if (domain == null)
            return null;
        BudgetEntity entity = new BudgetEntity();
        entity.setId(domain.getId());
        entity.setLimitAmount(domain.getLimitAmount());
        entity.setMonth(domain.getMonth());
        entity.setYear(domain.getYear());
        // Relationships (User and Category) should be handled by the RepositoryAdapter
        return entity;
    }
}
