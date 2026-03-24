package com.atomik.atomik_api.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.Budget;

public interface BudgetRepository {
    Optional<Budget> findById(UUID id);

    List<Budget> findAllByUserId(UUID userId);

    Budget save(Budget budget);

    void delete(Budget budget);

    Optional<Budget> update(Budget budget);
}
