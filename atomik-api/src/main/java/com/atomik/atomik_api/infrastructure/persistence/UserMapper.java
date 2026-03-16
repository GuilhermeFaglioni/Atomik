package com.atomik.atomik_api.infrastructure.persistence;

import com.atomik.atomik_api.domain.model.Email;
import com.atomik.atomik_api.domain.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    default Email map(String value) {
        return value != null ? new Email(value) : null;
    }

    default String map(Email email) {
        return email != null ? email.value() : null;
    }
}
