package com.atomik.atomik_api.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Category;

@Component
public class CategoryMapper {
    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return new Category(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getIcon(),
                entity.getColor(),
                entity.getIsDefault()
        );
    }

    public CategoryEntity toEntity(Category domain) {
        return toEntity(domain, null);
    }

    public CategoryEntity toEntity(Category domain, UserEntity userEntity) {
        if (domain == null) return null;
        CategoryEntity entity = new CategoryEntity();
        entity.setId(domain.getId());
        entity.setUser(userEntity);
        entity.setName(domain.getName());
        entity.setIcon(domain.getIcon());
        entity.setColor(domain.getColor());
        entity.setIsDefault(domain.getIsDefault());
        return entity;
    }
}
