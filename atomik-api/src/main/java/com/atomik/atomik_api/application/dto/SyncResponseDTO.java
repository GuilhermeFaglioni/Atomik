package com.atomik.atomik_api.application.dto;

import java.util.List;

public record SyncResponseDTO(
        List<SyncResultDTO> results,
        List<AccountBalanceDTO> updatedBalances) {

}
