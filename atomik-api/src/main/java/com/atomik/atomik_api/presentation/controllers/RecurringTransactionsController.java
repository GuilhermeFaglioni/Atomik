package com.atomik.atomik_api.presentation.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.usecases.CreateRecurringTransactionUseCase;

@RestController
@RequestMapping("/recurring-transactions")
public class RecurringTransactionsController {
    private final CreateRecurringTransactionUseCase recurringTransactionUseCase;

    public RecurringTransactionsController(CreateRecurringTransactionUseCase recurringTransactionUseCase) {
        this.recurringTransactionUseCase = recurringTransactionUseCase;
    }
}
