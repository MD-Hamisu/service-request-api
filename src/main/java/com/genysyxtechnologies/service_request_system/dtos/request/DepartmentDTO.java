package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Code is required") String code
) {}
