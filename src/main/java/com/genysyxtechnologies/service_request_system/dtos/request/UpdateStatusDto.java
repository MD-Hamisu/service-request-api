package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotNull;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;

public record UpdateStatusDto(
    @NotNull(message = "Status is required") ServiceRequestStatus status,
    String rejectionReason
) {

}
