package com.atomik.atomik_api.infrastructure.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.UserRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;
import com.atomik.atomik_api.infrastructure.persistence.UserMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class DatabaseUserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

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
}
