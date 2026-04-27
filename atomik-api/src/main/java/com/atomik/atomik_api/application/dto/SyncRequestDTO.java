package com.atomik.atomik_api.application.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SyncRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotNull(message = "Transactions are required") List<@Valid SyncTransactionItemDTO> transactions) {

}
