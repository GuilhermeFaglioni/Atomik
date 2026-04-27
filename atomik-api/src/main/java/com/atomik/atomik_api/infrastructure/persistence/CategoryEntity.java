package com.atomik.atomik_api.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
public class CategoryEntity {
    @Id
    @Column(columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    protected CategoryEntity() {
    }

    public CategoryEntity(UUID id, UserEntity user, String name, String icon, String color, Boolean isDefault) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.isDefault = isDefault;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }
}
