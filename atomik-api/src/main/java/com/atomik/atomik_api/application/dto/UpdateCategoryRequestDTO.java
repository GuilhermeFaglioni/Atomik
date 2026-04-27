package com.atomik.atomik_api.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Icon is required") String icon,
        @NotBlank(message = "Color is required") String color,
        @NotNull(message = "isDefault is required") Boolean isDefault) {
}
