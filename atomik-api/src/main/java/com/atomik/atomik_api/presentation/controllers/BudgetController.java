package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final CreateBudgetUseCase createBudgetUseCase;
    private final GetBudgetUseCase getBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;
    private final ListUserBudgetsUseCase listUserBudgetsUseCase;

    public BudgetController(CreateBudgetUseCase createBudgetUseCase, GetBudgetUseCase getBudgetUseCase,
            UpdateBudgetUseCase updateBudgetUseCase, DeleteBudgetUseCase deleteBudgetUseCase,
            ListUserBudgetsUseCase listUserBudgetsUseCase) {
        this.createBudgetUseCase = createBudgetUseCase;
        this.getBudgetUseCase = getBudgetUseCase;
        this.updateBudgetUseCase = updateBudgetUseCase;
        this.deleteBudgetUseCase = deleteBudgetUseCase;
        this.listUserBudgetsUseCase = listUserBudgetsUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDTO> createBudget(@RequestBody CreateBudgetRequestDTO request) {
        var response = createBudgetUseCase.execute(request.userId(), request.categoryId(), request.limitAmount(),
                request.month(), request.year(), request.name());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<BudgetResponseDTO> getBudget(@PathVariable String userId, @PathVariable String id) {
        var response = getBudgetUseCase.execute(userId, id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(@PathVariable String userId, @PathVariable String id,
            @RequestBody UpdateBudgetRequestDTO request) {
        var response = updateBudgetUseCase.execute(userId, id, request.categoryId(), request.name(),
                request.limitAmount(), request.month(), request.year());
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String userId, @PathVariable String id) {
        deleteBudgetUseCase.execute(userId, id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<BudgetResponseDTO>> listUserBudgets(@PathVariable String userId) {
        var response = listUserBudgetsUseCase.execute(userId);
        return ResponseEntity.status(200).body(response);
    }

}
