package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBudgetRequestDTO(
        @NotBlank(message = "User ID is required") String userId,
        @NotBlank(message = "Budget ID is required") String id,
        @NotBlank(message = "Category ID is required") String categoryId,
        @NotNull(message = "Limit amount is required") BigDecimal limitAmount,
        @NotNull(message = "Year is required") Integer year,
        @NotNull(message = "Month is required") Integer month,
        @NotBlank(message = "Name is required") String name) {

}
