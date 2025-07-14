package com.md.service_request_api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ServiceOfferingDTO(
        @NotBlank(message = "Service name is required") String name,
        String description,
        @NotNull(message = "Category ID is required") Long categoryId,
        @NotNull(message = "Department ID is required") Long departmentId,
        boolean isActive
) {}
