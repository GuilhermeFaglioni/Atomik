package com.atomik.atomik_api.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBudgetRepository extends JpaRepository<BudgetEntity, UUID> {
    List<BudgetEntity> findByUser_Id(UUID userId);

    Optional<BudgetEntity> findById(UUID id);
}
