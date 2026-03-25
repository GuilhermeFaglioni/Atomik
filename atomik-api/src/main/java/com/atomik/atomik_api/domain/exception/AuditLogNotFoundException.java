package com.atomik.atomik_api.domain.exception;

public class AuditLogNotFoundException extends RuntimeException {
    public AuditLogNotFoundException(String message) {
        super(message);
    }
}
