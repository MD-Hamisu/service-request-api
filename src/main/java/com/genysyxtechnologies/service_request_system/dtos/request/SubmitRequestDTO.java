package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitRequestDTO(
        @NotBlank(message = "Request data is required") String requestData,
        @NotNull(message = "User department ID is required") Long userDepartmentId
) {}
