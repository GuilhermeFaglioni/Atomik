package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.BudgetResponseDTO;
import com.atomik.atomik_api.domain.exception.BudgetNotFoundException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Budget;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class ListUserBudgetsUseCase {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public ListUserBudgetsUseCase(UserRepository userRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetResponseDTO> execute(String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var budgets = budgetRepository.findAllByUserId(UUID.fromString(userId));

        if (budgets.isEmpty()) {
            throw new BudgetNotFoundException("Budgets not found");
        }

        return budgets.stream().map(this::toResponseDTO).toList();

    }

    private BudgetResponseDTO toResponseDTO(Budget budget) {
        return new BudgetResponseDTO(budget.getId().toString(), budget.getName(), budget.getLimitAmount(),
                budget.getMonth(), budget.getYear());
    }
}
