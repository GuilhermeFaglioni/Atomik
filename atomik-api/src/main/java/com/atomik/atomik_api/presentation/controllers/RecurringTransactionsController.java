package com.atomik.atomik_api.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.CreateRecurringRequestDTO;
import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.application.dto.RecurringStatusUpdateRequestDTO;
import com.atomik.atomik_api.application.usecases.CreateRecurringTransactionUseCase;
import com.atomik.atomik_api.application.usecases.DeleteRecurringTransactionUseCase;
import com.atomik.atomik_api.application.usecases.GetRecurringTransactionById;
import com.atomik.atomik_api.application.usecases.GetRecurringTransactionsByUserUseCase;
import com.atomik.atomik_api.application.usecases.UpdateRecurringTransactionStatus;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recurring-transactions")
public class RecurringTransactionsController {
    private final CreateRecurringTransactionUseCase createRecurringTransactionUseCase;
    private final GetRecurringTransactionsByUserUseCase getRecurringTransactionsByUserUseCase;
    private final GetRecurringTransactionById getRecurringTransactionById;
    private final UpdateRecurringTransactionStatus updateRecurringTransactionStatus;
    private final DeleteRecurringTransactionUseCase deleteRecurringTransactionUseCase;
    private final AuthenticatedUserService authenticatedUserService;

    public RecurringTransactionsController(CreateRecurringTransactionUseCase createRecurringTransactionUseCase,
            GetRecurringTransactionsByUserUseCase getRecurringTransactionsByUserUseCase,
            GetRecurringTransactionById getRecurringTransactionById,
            UpdateRecurringTransactionStatus updateRecurringTransactionStatus,
            DeleteRecurringTransactionUseCase deleteRecurringTransactionUseCase,
            AuthenticatedUserService authenticatedUserService) {
        this.createRecurringTransactionUseCase = createRecurringTransactionUseCase;
        this.getRecurringTransactionsByUserUseCase = getRecurringTransactionsByUserUseCase;
        this.getRecurringTransactionById = getRecurringTransactionById;
        this.updateRecurringTransactionStatus = updateRecurringTransactionStatus;
        this.deleteRecurringTransactionUseCase = deleteRecurringTransactionUseCase;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping
    public ResponseEntity<RecurringResponseDTO> createRecurringTransaction(
            @RequestBody @Valid CreateRecurringRequestDTO request,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
        CreateRecurringRequestDTO authenticatedRequest = new CreateRecurringRequestDTO(authenticatedUserId,
                request.categoryId(), request.sourceAccountId(), request.destinationAccountId(), request.amount(),
                request.description(), request.type(), request.startDate(), request.endDate(), request.frequency());
        return ResponseEntity.status(201).body(createRecurringTransactionUseCase.execute(authenticatedRequest));
    }

    @GetMapping
    public ResponseEntity<List<RecurringResponseDTO>> listRecurringTransactions(Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(getRecurringTransactionsByUserUseCase.execute(authenticatedUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringResponseDTO> getRecurringTransaction(@PathVariable String id,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(getRecurringTransactionById.execute(id, authenticatedUserId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RecurringResponseDTO> updateRecurringTransactionStatus(@PathVariable String id,
            @RequestBody @Valid RecurringStatusUpdateRequestDTO request,
            Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(updateRecurringTransactionStatus.execute(authenticatedUserId, id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringTransaction(@PathVariable String id, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.getAuthenticatedUserId(authentication);
        deleteRecurringTransactionUseCase.execute(authenticatedUserId, id);
        return ResponseEntity.noContent().build();
    }
}
