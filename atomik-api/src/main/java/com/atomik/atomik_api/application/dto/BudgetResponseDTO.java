package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;

public record BudgetResponseDTO(
        String id,
        String name,
        BigDecimal limitAmount,
        Integer month,
        Integer year) {
}
