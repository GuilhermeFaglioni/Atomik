package com.atomik.atomik_api.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUser_Id(UUID userId);

    boolean existsByNameAndUser_Id(String name, UUID userId);
}
