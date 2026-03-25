package com.atomik.atomik_api.presentation.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atomik.atomik_api.application.dto.AuditLogResponseDTO;
import com.atomik.atomik_api.application.usecases.GetAuditLogByIdUseCase;
import com.atomik.atomik_api.application.usecases.ListAllUserAuditLogs;
import com.atomik.atomik_api.application.usecases.ListUserAuditLogsByDateUseCase;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {
    private final ListAllUserAuditLogs listAllUserAuditLogs;
    private final ListUserAuditLogsByDateUseCase listUserAuditLogsByDateUseCase;
    private final GetAuditLogByIdUseCase getAuditLogByIdUseCase;

    public AuditLogController(
            ListAllUserAuditLogs listAllUserAuditLogs,
            ListUserAuditLogsByDateUseCase listUserAuditLogsByDateUseCase,
            GetAuditLogByIdUseCase getAuditLogByIdUseCase) {
        this.listAllUserAuditLogs = listAllUserAuditLogs;
        this.listUserAuditLogsByDateUseCase = listUserAuditLogsByDateUseCase;
        this.getAuditLogByIdUseCase = getAuditLogByIdUseCase;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> listAll(@PathVariable String userId) {
        var response = listAllUserAuditLogs.execute(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<AuditLogResponseDTO>> searchByPeriod(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var response = listUserAuditLogsByDateUseCase.execute(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponseDTO> getById(@PathVariable String id) {
        var response = getAuditLogByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
