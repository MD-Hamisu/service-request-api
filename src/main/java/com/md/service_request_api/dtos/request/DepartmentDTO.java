package com.md.service_request_api.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Code is required") String code
) {}
