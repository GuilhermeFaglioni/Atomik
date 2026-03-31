package com.atomik.atomik_api.application.dto;

import java.math.BigDecimal;

public record AccountBalanceDTO(
        String accountId,
        BigDecimal currentBalance) {

}
