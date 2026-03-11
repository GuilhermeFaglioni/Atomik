package com.atomik.atomik_api.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;

import lombok.AllArgsConstructor;

import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private final String icon;
    private final String color;
    private final Boolean isDefault;

    public static Category createNewCategory(UUID userId, String name, String icon, String color, Boolean isDefault) {
        Category category = new Category(
                UUID.randomUUID(),
                userId,
                name,
                icon,
                color,
                isDefault != null ? isDefault : false);
        category.validate();
        return category;
    }

    public void validate() {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}
