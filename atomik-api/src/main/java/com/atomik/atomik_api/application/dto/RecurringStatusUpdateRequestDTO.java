package com.atomik.atomik_api.application.dto;

import com.atomik.atomik_api.domain.model.RecurringStatus;

import jakarta.validation.constraints.NotNull;

public record RecurringStatusUpdateRequestDTO(
        @NotNull(message = "Status is required") RecurringStatus status) {
}
