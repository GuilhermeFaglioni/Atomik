package com.atomik.atomik_api.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.Account;

public interface AccountRepository {

    boolean existsByNameAndUserId(String name, UUID userId);

    Account save(Account account);

    Optional<Account> findById(UUID id);

    List<Account> findByUserId(UUID userId);

    Optional<Account> update(Account account);

    void delete(Account account);
}
