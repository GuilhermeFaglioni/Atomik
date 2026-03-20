package com.atomik.atomik_api.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.CreateTransferRequestDTO;
import com.atomik.atomik_api.application.dto.CreateUniqueTransactionRequestDTO;
import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.application.usecases.CreateTransferUseCase;
import com.atomik.atomik_api.application.usecases.CreateUniqueTransactionUseCase;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final CreateUniqueTransactionUseCase createUniqueTransactionUseCase;
    private final CreateTransferUseCase createTransferUseCase;

    public TransactionController(CreateUniqueTransactionUseCase createUniqueTransactionUseCase,
            CreateTransferUseCase createTransferUseCase) {
        this.createUniqueTransactionUseCase = createUniqueTransactionUseCase;
        this.createTransferUseCase = createTransferUseCase;
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

}
