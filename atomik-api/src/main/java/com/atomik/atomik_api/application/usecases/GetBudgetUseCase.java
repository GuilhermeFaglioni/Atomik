package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.BudgetResponseDTO;
import com.atomik.atomik_api.domain.exception.BudgetNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Budget;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class GetBudgetUseCase {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public GetBudgetUseCase(UserRepository userRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public BudgetResponseDTO execute(String userId, String id) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Budget budget = budgetRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        if (!user.getId().equals(budget.getUserId())) {
            throw new UnauthorizedException("User and budget do not match");
        }

        return toResponseDTO(budget);
    }

    private BudgetResponseDTO toResponseDTO(Budget budget) {
        return new BudgetResponseDTO(budget.getId().toString(), budget.getName(), budget.getLimitAmount(),
                budget.getMonth(), budget.getYear());
    }
}
