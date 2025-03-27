package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ServiceOfferingDTO(
        @NotBlank(message = "Name is Required") String name,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Category is required") Long categoryId,
        @NotBlank(message = "Field Schema is required") String fields, // JSON string
        boolean isActive // default is true on creation
) {
}
