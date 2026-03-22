package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.DeleteCategoryResponseDTO;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class DeleteCategoryUseCase {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public DeleteCategoryResponseDTO execute(String userId, String categoryId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!category.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("User not authorized");
        }

        categoryRepository.delete(category);

        return new DeleteCategoryResponseDTO(category.getId().toString(), "Category deleted successfully");
    }
}
