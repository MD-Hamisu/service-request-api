package com.md.service_request_api.dtos.request;

import jakarta.validation.constraints.NotNull;

import com.md.service_request_api.constant.ServiceRequestStatus;

public record UpdateStatusDto(
    @NotNull(message = "Status is required") ServiceRequestStatus status,
    String rejectionReason
) {

}
