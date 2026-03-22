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

import com.atomik.atomik_api.application.dto.CreateTransferRequestDTO;
import com.atomik.atomik_api.application.dto.CreateUniqueTransactionRequestDTO;
import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.application.dto.TransactionResponseDTO;
import com.atomik.atomik_api.application.dto.UpdateTransactionRequestDTO;
import com.atomik.atomik_api.application.usecases.CreateTransferUseCase;
import com.atomik.atomik_api.application.usecases.CreateUniqueTransactionUseCase;
import com.atomik.atomik_api.application.usecases.DeleteTransactionUseCase;
import com.atomik.atomik_api.application.usecases.GetTransactionUseCase;
import com.atomik.atomik_api.application.usecases.ListUserTransactionUseCase;
import com.atomik.atomik_api.application.usecases.UpdateTransactionUseCase;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final CreateUniqueTransactionUseCase createUniqueTransactionUseCase;
    private final CreateTransferUseCase createTransferUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final ListUserTransactionUseCase listUserTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public TransactionController(CreateUniqueTransactionUseCase createUniqueTransactionUseCase,
            CreateTransferUseCase createTransferUseCase, GetTransactionUseCase getTransactionUseCase,
            ListUserTransactionUseCase listUserTransactionUseCase, UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase) {
        this.createUniqueTransactionUseCase = createUniqueTransactionUseCase;
        this.createTransferUseCase = createTransferUseCase;
        this.getTransactionUseCase = getTransactionUseCase;
        this.listUserTransactionUseCase = listUserTransactionUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    @PostMapping("/unique")
    public ResponseEntity<TransactionCreatedResponse> createUniqueTransaction(
            @RequestBody CreateUniqueTransactionRequestDTO request) {
        var response = createUniqueTransactionUseCase.execute(request.userId(), request.categoryId(),
                request.accountId(),
                request.amount(), request.description(), request.date(), request.getType());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionCreatedResponse> createTransfer(@RequestBody CreateTransferRequestDTO request) {
        var response = createTransferUseCase.execute(request.userId(), request.categoryId(),
                request.sourceAccountId(),
                request.destinationAccountId(), request.amount(), request.description(), request.date());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(@PathVariable String userId,
            @PathVariable String id) {
        var response = getTransactionUseCase.execute(userId, id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(@PathVariable String userId,
            @PathVariable String id,
            @RequestBody UpdateTransactionRequestDTO request) {
        var response = updateTransactionUseCase.execute(id, userId, request.categoryId(), request.sourceAccountId(),
                request.destinationAccountId(), request.amount(), request.description(), request.getDate(),
                request.getType());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> listUserTransactions(@PathVariable String userId) {
        var response = listUserTransactionUseCase.execute(userId);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String userId, @PathVariable String id) {
        deleteTransactionUseCase.execute(userId, id);
        return ResponseEntity.status(204).build();
    }

}
