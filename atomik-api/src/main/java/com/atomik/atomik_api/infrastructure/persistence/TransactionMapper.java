package com.atomik.atomik_api.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.atomik.atomik_api.domain.model.Transaction;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "sourceAccountId", source = "sourceAccount.id")
    @Mapping(target = "destinationAccountId", source = "destinationAccount.id")
    Transaction toDomain(TransactionEntity transactionEntity);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "sourceAccount.id", source = "sourceAccountId")
    @Mapping(target = "destinationAccount.id", source = "destinationAccountId")
    TransactionEntity toEntity(Transaction transaction);
}
