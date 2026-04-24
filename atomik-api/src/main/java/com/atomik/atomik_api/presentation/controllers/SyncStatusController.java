package com.atomik.atomik_api.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.atomik.atomik_api.application.dto.SyncRequestDTO;
import com.atomik.atomik_api.application.dto.SyncResponseDTO;
import com.atomik.atomik_api.application.usecases.SyncTransactionsUseCase;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

@RestController
@RequestMapping("/sync")
public class SyncStatusController {
    private final SyncTransactionsUseCase syncTransactionsUseCase;
    private final AuthenticatedUserService authenticatedUserService;

    public SyncStatusController(SyncTransactionsUseCase syncTransactionsUseCase,
            AuthenticatedUserService authenticatedUserService) {
        this.syncTransactionsUseCase = syncTransactionsUseCase;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping
    public ResponseEntity<SyncResponseDTO> sync(@RequestBody SyncRequestDTO request, Authentication authentication) {
        String authenticatedUserId = authenticatedUserService.requireCurrentUser(authentication, request.userId());
        SyncRequestDTO sanitizedRequest = new SyncRequestDTO(authenticatedUserId, request.transactions());
        SyncResponseDTO response = syncTransactionsUseCase.execute(sanitizedRequest);
        return ResponseEntity.status(200).body(response);
    }
}
