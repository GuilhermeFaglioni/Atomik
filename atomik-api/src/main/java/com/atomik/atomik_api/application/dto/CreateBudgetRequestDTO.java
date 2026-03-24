package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBudgetRequestDTO(
        @NotBlank(message = "UserId is requred") String userId,
        @NotBlank(message = "CategoryId is requred") String categoryId,
        @NotNull(message = "LimitAmount is requred") BigDecimal limitAmount,
        @NotNull(message = "Month is requred") Integer month,
        @NotNull(message = "Year is requred") Integer year,
        @NotBlank(message = "Name is requred") String name) {

}