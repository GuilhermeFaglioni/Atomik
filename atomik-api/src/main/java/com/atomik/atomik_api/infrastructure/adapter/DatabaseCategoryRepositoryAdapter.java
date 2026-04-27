package com.atomik.atomik_api.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.infrastructure.persistence.CategoryMapper;
import com.atomik.atomik_api.infrastructure.persistence.JpaCategoryRepository;
import com.atomik.atomik_api.infrastructure.persistence.JpaUserRepository;

@Component
public class DatabaseCategoryRepositoryAdapter implements CategoryRepository {
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaUserRepository jpaUserRepository;
    private final CategoryMapper categoryMapper;

    public DatabaseCategoryRepositoryAdapter(JpaCategoryRepository jpaCategoryRepository,
            JpaUserRepository jpaUserRepository,
            CategoryMapper categoryMapper) {
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaCategoryRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findAllByUserId(UUID userId) {
        return jpaCategoryRepository.findAllByUser_Id(userId).stream().map(categoryMapper::toDomain).toList();
    }

    @Override
    public Category save(Category category) {
        var userRef = jpaUserRepository.getReferenceById(category.getUserId());
        var entity = categoryMapper.toEntity(category, userRef);
        return categoryMapper.toDomain(jpaCategoryRepository.save(entity));
    }

    @Override
    public void delete(Category category) {
        jpaCategoryRepository.deleteById(category.getId());
    }

    @Override
    public Optional<Category> update(Category category) {
        return jpaCategoryRepository.findById(category.getId()).map(existingEntity -> {
            existingEntity.setName(category.getName());
            existingEntity.setIcon(category.getIcon());
            existingEntity.setColor(category.getColor());
            existingEntity.setIsDefault(category.getIsDefault());
            return categoryMapper.toDomain(jpaCategoryRepository.save(existingEntity));
        });
    }
}
