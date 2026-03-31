package com.atomik.atomik_api.application.dto;

import java.util.List;

public record SyncRequestDTO(
        String userId,
        List<SyncTransactionItemDTO> transactions) {

}
