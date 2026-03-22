package com.atomik.atomik_api.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.atomik.atomik_api.domain.model.Category;

public interface CategoryRepository {
    Optional<Category> findById(UUID id);

    Category save(Category category);

    void delete(Category category);

    List<Category> findAllByUserId(UUID userId);

    Optional<Category> update(Category category);
}
