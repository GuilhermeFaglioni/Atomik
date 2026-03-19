package com.atomik.atomik_api.infrastructure.persistence;

import com.atomik.atomik_api.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "userId", source = "user.id")
    Account toDomain(AccountEntity entity);

    @Mapping(target = "user", ignore = true)
    AccountEntity toEntity(Account domain);
}
