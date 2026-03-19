package com.atomik.atomik_api.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.infrastructure.persistence.AccountEntity;
import com.atomik.atomik_api.infrastructure.persistence.AccountMapper;
import com.atomik.atomik_api.infrastructure.persistence.JpaAccountRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepository {
    private final JpaAccountRepository jpaAccountRepository;
    private final JpaUserRepository jpaUserRepository;
    private final AccountMapper mapper;

    @Override
    public List<Account> findByUserId(UUID userId) {
        List<AccountEntity> entities = jpaAccountRepository.findByUserId(userId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        Optional<AccountEntity> entity = jpaAccountRepository.findById(id);
        return entity.map(mapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        var userRef = jpaUserRepository.getReferenceById(account.getUserId());
        entity.setUser(userRef);
        return mapper.toDomain(jpaAccountRepository.save(entity));
    }

    @Override
    public Optional<Account> update(Account account) {
        return jpaAccountRepository.findById(account.getId()).map(existingEntity -> {
            existingEntity.setName(account.getName());
            existingEntity.setCurrency(account.getCurrency());
            existingEntity.setType(account.getType());
            return mapper.toDomain(jpaAccountRepository.save(existingEntity));
        });
    }

    @Override
    public void delete(Account account) {
        jpaAccountRepository.deleteById(account.getId());
    }

    @Override
    public boolean existsByNameAndUserId(String name, UUID userId) {
        return jpaAccountRepository.existsByNameAndUserId(name, userId);
    }
}
