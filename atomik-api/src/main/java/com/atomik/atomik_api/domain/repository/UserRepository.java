package com.atomik.atomik_api.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.User;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    void save(User user);

    Optional<User> findById(UUID id);

    void delete(User user);

    Optional<User> update(User user);

}