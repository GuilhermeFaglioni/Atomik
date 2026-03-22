package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.CategoryResponseDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateCategoryUseCase {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CreateCategoryUseCase(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDTO execute(String userId, String name, String icon, String color, Boolean isDefault) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var category = Category.createNewCategory(UUID.fromString(userId), name, icon, color, isDefault);
        categoryRepository.save(category);
        return toResponse(category);
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId().toString(), category.getName(), category.getIcon(),
                category.getColor(), category.getIsDefault());
    }
}