package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.BudgetResponseDTO;
import com.atomik.atomik_api.domain.exception.BudgetNotFoundException;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Budget;
import com.atomik.atomik_api.domain.model.Category;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class UpdateBudgetUseCase {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public UpdateBudgetUseCase(UserRepository userRepository, CategoryRepository categoryRepository,
            BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    public BudgetResponseDTO execute(String userId, String id, String categoryId, String name, BigDecimal limitAmount,
            Integer month,
            Integer year) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!user.getId().equals(category.getUserId())) {
            throw new UnauthorizedException("User and category do not match");
        }

        var budget = budgetRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        if (!user.getId().equals(budget.getUserId())) {
            throw new UnauthorizedException("User and budget do not match");
        }

        var updatedBudget = new Budget(budget.getId(), budget.getUserId(), UUID.fromString(categoryId), limitAmount,
                month, year, name);

        updatedBudget.validate();

        var savedBudget = budgetRepository.update(updatedBudget)
                .orElseThrow(() -> new RuntimeException("Error updating budget"));

        return toResponseDTO(savedBudget);
    }

    private BudgetResponseDTO toResponseDTO(Budget budget) {
        return new BudgetResponseDTO(budget.getId().toString(), budget.getName(), budget.getLimitAmount(),
                budget.getMonth(), budget.getYear());
    }
}
