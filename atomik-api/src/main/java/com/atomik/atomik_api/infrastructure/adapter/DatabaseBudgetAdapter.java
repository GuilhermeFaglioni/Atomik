package com.atomik.atomik_api.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Budget;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.infrastructure.persistence.BudgetMapper;
import com.atomik.atomik_api.infrastructure.persistence.JpaBudgetRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaCategoryRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;

@Component
public class DatabaseBudgetAdapter implements BudgetRepository {
    private final JpaBudgetRepository jpaBudgetRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final BudgetMapper budgetMapper;

    public DatabaseBudgetAdapter(JpaBudgetRepository jpaBudgetRepository, JpaUserRepository jpaUserRepository,
            JpaCategoryRepository jpaCategoryRepository, BudgetMapper budgetMapper) {
        this.jpaBudgetRepository = jpaBudgetRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.budgetMapper = budgetMapper;
    }

    @Override
    public List<Budget> findAllByUserId(UUID userId) {
        return jpaBudgetRepository.findByUser_Id(userId).stream().map(budgetMapper::toDomain).toList();
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return jpaBudgetRepository.findById(id).map(budgetMapper::toDomain);
    }

    @Override
    public Budget save(Budget budget) {
        var userRef = jpaUserRepository.getReferenceById(budget.getUserId());
        var categoryRef = jpaCategoryRepository.getReferenceById(budget.getCategoryId());
        var entity = budgetMapper.toEntity(budget, userRef, categoryRef);
        jpaBudgetRepository.save(entity);
        return budgetMapper.toDomain(entity);
    }

    @Override
    public void delete(Budget budget) {
        var userRef = jpaUserRepository.getReferenceById(budget.getUserId());
        var categoryRef = jpaCategoryRepository.getReferenceById(budget.getCategoryId());
        var entity = budgetMapper.toEntity(budget, userRef, categoryRef);
        jpaBudgetRepository.delete(entity);
    }

    @Override
    public Optional<Budget> update(Budget budget) {
        return jpaBudgetRepository.findById(budget.getId()).map(existingEntity -> {
            existingEntity.setLimitAmount(budget.getLimitAmount());
            existingEntity.setMonth(budget.getMonth());
            existingEntity.setYear(budget.getYear());
            existingEntity.setName(budget.getName());
            return budgetMapper.toDomain(jpaBudgetRepository.save(existingEntity));
        });
    }
}
