package com.atomik.atomik_api.application.dto;

import java.time.LocalDateTime;

public record AuditLogResponseDTO(
        String id,
        String transactionId,
        String fieldChanged,
        String oldValue,
        String newValue,
        LocalDateTime changedAt) {

}
