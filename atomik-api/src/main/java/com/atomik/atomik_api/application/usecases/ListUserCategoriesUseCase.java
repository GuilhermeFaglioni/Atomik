package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.CategoryResponseDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class ListUserCategoriesUseCase {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ListUserCategoriesUseCase(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryResponseDTO> execute(String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));

        var categories = categoryRepository.findAllByUserId(UUID.fromString(userId));

        return categories.stream().map(this::toResponse).toList();
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId().toString(), category.getName(), category.getIcon(),
                category.getColor(), category.getIsDefault());
    }
}
