package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.CategoryResponseDTO;
import com.atomik.atomik_api.application.dto.CreateCategoryRequestDTO;
import com.atomik.atomik_api.application.dto.UpdateCategoryRequestDTO;
import com.atomik.atomik_api.application.usecases.CreateCategoryUseCase;
import com.atomik.atomik_api.application.usecases.DeleteCategoryUseCase;
import com.atomik.atomik_api.application.usecases.GetCategoryUseCase;
import com.atomik.atomik_api.application.usecases.ListUserCategoriesUseCase;
import com.atomik.atomik_api.application.usecases.UpdateCategoryUseCase;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListUserCategoriesUseCase listUserCategoriesUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase, GetCategoryUseCase getCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase, DeleteCategoryUseCase deleteCategoryUseCase,
            ListUserCategoriesUseCase listUserCategoriesUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.listUserCategoriesUseCase = listUserCategoriesUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CreateCategoryRequestDTO request) {
        var response = createCategoryUseCase.execute(request.userId(), request.name(), request.icon(),
                request.color(), request.isDefault());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable String userId, @PathVariable String id) {
        var response = getCategoryUseCase.execute(userId, id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable String userId, @PathVariable String id,
            @RequestBody UpdateCategoryRequestDTO request) {
        var response = updateCategoryUseCase.execute(userId, id, request.name(), request.icon(), request.color(),
                request.isDefault());
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String userId, @PathVariable String id) {
        deleteCategoryUseCase.execute(userId, id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CategoryResponseDTO>> listUserCategories(@PathVariable String userId) {
        var response = listUserCategoriesUseCase.execute(userId);
        return ResponseEntity.status(200).body(response);
    }
}
