package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Account;

@Component
public class AccountMapper {

    public Account toDomain(AccountEntity entity) {
        if (entity == null)
            return null;
        return new Account(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getName(),
                entity.getType(),
                entity.getCurrency(),
                entity.getCreatedAt(),
                entity.getBalance());
    }

    public AccountEntity toEntity(Account domain) {
        if (domain == null)
            return null;
        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setType(domain.getType());
        entity.setCurrency(domain.getCurrency());
        entity.setCreatedAt(domain.getCreatedAt());
        // User relation should be handled by the RepositoryAdapter
        return entity;
    }
}
