package com.atomik.atomik_api.infrastructure.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.atomik.atomik_api.domain.model.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "userId", source = "user.id")
    Category toDomain(CategoryEntity categoryEntity);

    @Mapping(target = "user.id", source = "userId")
    CategoryEntity toEntity(Category category);
}
