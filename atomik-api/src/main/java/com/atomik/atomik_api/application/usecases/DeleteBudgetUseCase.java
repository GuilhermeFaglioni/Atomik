package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.DeleteBudgetResponseDTO;
import com.atomik.atomik_api.domain.exception.BudgetNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.repository.BudgetRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class DeleteBudgetUseCase {
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public DeleteBudgetUseCase(UserRepository userRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public DeleteBudgetResponseDTO execute(String userId, String id) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var budget = budgetRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        if (!budget.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("User and budget do not match");
        }

        budgetRepository.delete(budget);

        return new DeleteBudgetResponseDTO(id, "Budget deleted successfully");
    }
}
