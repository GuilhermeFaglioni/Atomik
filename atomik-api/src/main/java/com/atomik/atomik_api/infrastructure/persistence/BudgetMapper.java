package com.atomik.atomik_api.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.atomik.atomik_api.domain.model.Budget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    Budget toDomain(BudgetEntity budgetEntity);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "category.id", source = "categoryId")
    BudgetEntity toEntity(Budget budget);
}
