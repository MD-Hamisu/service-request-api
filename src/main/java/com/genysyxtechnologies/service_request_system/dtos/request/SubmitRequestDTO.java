package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotNull;

public record SubmitRequestDTO(
        String description,
        @NotNull(message = "User department ID is required") Long userDepartmentId
) {}
