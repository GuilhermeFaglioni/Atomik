package com.atomik.atomik_api.infrastructure.persistence;

import com.atomik.atomik_api.domain.model.Email;
import com.atomik.atomik_api.domain.model.User;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getPreferredCurrency(),
                entity.getCreatedAt());
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setPreferredCurrency(domain.getPreferredCurrency());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
