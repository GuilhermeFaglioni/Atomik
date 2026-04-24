package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.BudgetResponseDTO;
import com.atomik.atomik_api.application.dto.CreateBudgetRequestDTO;
import com.atomik.atomik_api.application.dto.UpdateBudgetRequestDTO;
import com.atomik.atomik_api.application.usecases.CreateBudgetUseCase;
import com.atomik.atomik_api.application.usecases.DeleteBudgetUseCase;
import com.atomik.atomik_api.application.usecases.GetBudgetUseCase;
import com.atomik.atomik_api.application.usecases.ListUserBudgetsUseCase;
import com.atomik.atomik_api.application.usecases.UpdateBudgetUseCase;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final CreateBudgetUseCase createBudgetUseCase;
    private final GetBudgetUseCase getBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;
    private final ListUserBudgetsUseCase listUserBudgetsUseCase;
    private final AuthenticatedUserService authenticatedUserService;

    public BudgetController(CreateBudgetUseCase createBudgetUseCase, GetBudgetUseCase getBudgetUseCase,
            UpdateBudgetUseCase updateBudgetUseCase, DeleteBudgetUseCase deleteBudgetUseCase,
            ListUserBudgetsUseCase listUserBudgetsUseCase, AuthenticatedUserService authenticatedUserService) {
        this.createBudgetUseCase = createBudgetUseCase;
        this.getBudgetUseCase = getBudgetUseCase;
        this.updateBudgetUseCase = updateBudgetUseCase;
        this.deleteBudgetUseCase = deleteBudgetUseCase;
        this.listUserBudgetsUseCase = listUserBudgetsUseCase;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody CreateBudgetRequestDTO request,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, request.userId());
        var response = createBudgetUseCase.execute(authenticatedUserId, request.categoryId(), request.limitAmount(),
                request.month(), request.year(), request.name());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<BudgetResponseDTO> getBudget(@PathVariable String userId, @PathVariable String id,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = getBudgetUseCase.execute(authenticatedUserId, id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(@PathVariable String userId, @PathVariable String id,
            @RequestBody UpdateBudgetRequestDTO request, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = updateBudgetUseCase.execute(authenticatedUserId, id, request.categoryId(), request.name(),
                request.limitAmount(), request.month(), request.year());
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String userId, @PathVariable String id,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        deleteBudgetUseCase.execute(authenticatedUserId, id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<BudgetResponseDTO>> listUserBudgets(@PathVariable String userId,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, userId);
        var response = listUserBudgetsUseCase.execute(authenticatedUserId);
        return ResponseEntity.status(200).body(response);
    }

}
