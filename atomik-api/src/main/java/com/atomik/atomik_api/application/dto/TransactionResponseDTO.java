package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;

public record TransactionResponseDTO(String id, String type, BigDecimal amount, String description, String date) {
}
