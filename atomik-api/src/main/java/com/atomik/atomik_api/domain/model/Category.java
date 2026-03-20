package com.atomik.atomik_api.domain.model;

import java.util.UUID;

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

    public Category(UUID id, UUID userId, String name, String icon, String color, Boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isDefault = isDefault;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public Boolean getIsDefault() {
        return isDefault;
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
