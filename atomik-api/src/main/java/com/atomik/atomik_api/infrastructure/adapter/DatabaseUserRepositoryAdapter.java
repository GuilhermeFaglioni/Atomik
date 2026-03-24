package com.atomik.atomik_api.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;
import com.atomik.atomik_api.infrastructure.persistence.UserMapper;

@Component
public class DatabaseUserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    public DatabaseUserRepositoryAdapter(UserMapper userMapper, JpaUserRepository jpaUserRepository) {
        this.userMapper = userMapper;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public void save(User user) {
        var entity = userMapper.toEntity(user);
        jpaUserRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public void delete(User user) {
        jpaUserRepository.delete(userMapper.toEntity(user));
    }

    @Override
    public Optional<User> update(User user) {
        return jpaUserRepository.findById(user.getId()).map(existingEntity -> {
            existingEntity.setName(user.getName());
            existingEntity.setEmail(user.getEmail().value());
            existingEntity.setPasswordHash(user.getPasswordHash());
            existingEntity.setPreferredCurrency(user.getPreferredCurrency());
            return userMapper.toDomain(jpaUserRepository.save(existingEntity));
        });
    }
}