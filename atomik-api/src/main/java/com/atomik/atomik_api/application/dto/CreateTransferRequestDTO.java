package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record CreateTransferRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Category ID is required") String categoryId,
        @NotBlank(message = "Source Account ID is required") String sourceAccountId,
        @NotBlank(message = "Destination Account ID is required") String destinationAccountId,
        @NotBlank(message = "Amount is required") BigDecimal amount,
        @NotBlank(message = "Description is required") String description,
        @NotBlank(message = "Date is required") LocalDateTime date) {

}
