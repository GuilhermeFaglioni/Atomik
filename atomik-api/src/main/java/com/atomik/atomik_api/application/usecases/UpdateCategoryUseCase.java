package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.CategoryResponseDTO;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class UpdateCategoryUseCase {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCase(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDTO execute(String userId, String categoryId, String name, String icon, String color,
            Boolean isDefault) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!user.getId().equals(category.getUserId())) {
            throw new UnauthorizedException("User not authorized");
        }

        var updatedCategory = new Category(category.getId(), category.getUserId(), name, icon, color, isDefault);

        updatedCategory.validate();

        categoryRepository.update(updatedCategory)
                .orElseThrow(() -> new RuntimeException("Failed to update category"));

        return toResponse(updatedCategory);
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId().toString(), category.getName(), category.getIcon(),
                category.getColor(), category.getIsDefault());
    }
}
