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
                false // isDefault not in entity
        );
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        CategoryEntity entity = new CategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setIcon(domain.getIcon());
        entity.setColor(domain.getColor());
        return entity;
    }
}
