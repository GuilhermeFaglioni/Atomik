package com.atomik.atomik_api.domain.exception;

public class RecurringTransactionNotFoundException extends RuntimeException {
    public RecurringTransactionNotFoundException(String message) {
        super(message);
    }
}
