package com.atomik.atomik_api.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.SyncRequestDTO;
import com.atomik.atomik_api.application.dto.SyncResponseDTO;
import com.atomik.atomik_api.application.usecases.SyncTransactionsUseCase;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/sync")
public class SyncStatusController {
    private final SyncTransactionsUseCase syncTransactionsUseCase;

    public SyncStatusController(SyncTransactionsUseCase syncTransactionsUseCase) {
        this.syncTransactionsUseCase = syncTransactionsUseCase;
    }

    @PostMapping
    public ResponseEntity<SyncResponseDTO> sync(@RequestBody SyncRequestDTO request, Authentication authentication) {
        String authenticatedUserId = authentication.getName();
        if (!authenticatedUserId.equals(request.userId())) {
            return ResponseEntity.status(403).body(null);
        }

        SyncResponseDTO response = syncTransactionsUseCase.execute(request);
        return ResponseEntity.status(200).body(response);
    }
}
