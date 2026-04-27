package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record SyncTransactionItemDTO(
        String id,
        String categoryId,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String description,
        LocalDateTime date,
        String type,
        @NotBlank(message = "Operation type is required") String operationType) {

}
