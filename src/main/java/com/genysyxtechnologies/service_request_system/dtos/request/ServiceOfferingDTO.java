package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceOfferingDTO {
    @NotBlank(message = "Name is Required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Category is required")
    private Long categoryId;
    @NotBlank(message = "Field Schema is required")
    private String fields; // JSON string
    private boolean isActive; // default is true on creation
}
