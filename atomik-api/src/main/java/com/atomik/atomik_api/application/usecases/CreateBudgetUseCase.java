package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.BudgetResponseDTO;
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
public class CreateBudgetUseCase {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CreateBudgetUseCase(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public BudgetResponseDTO execute(String userId, String categoryId, BigDecimal limitAmount, Integer month,
            Integer year, String name) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Category category = categoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!user.getId().equals(category.getUserId())) {
            throw new UnauthorizedException("User and category do not match");
        }

        var budget = Budget.createNewBudget(user.getId(), category.getId(), limitAmount, month, year, name);
        budgetRepository.save(budget);

        return toResponseDTO(budget);
    }

    private BudgetResponseDTO toResponseDTO(Budget budget) {
        return new BudgetResponseDTO(budget.getId().toString(), budget.getName(), budget.getLimitAmount(),
                budget.getMonth(), budget.getYear());
    }
}
