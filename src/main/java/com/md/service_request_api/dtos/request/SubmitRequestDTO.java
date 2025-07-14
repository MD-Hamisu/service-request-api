package com.md.service_request_api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitRequestDTO(
        @NotBlank(message = "Request data required") String requestData,
        @NotNull(message = "User department ID is required") Long userDepartmentId
) {}
