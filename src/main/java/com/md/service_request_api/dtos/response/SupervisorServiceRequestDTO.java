package com.md.service_request_api.dtos.response;

import com.md.service_request_api.constant.ServiceRequestStatus;

import java.time.LocalDateTime;

public record SupervisorServiceRequestDTO(
        Long id,
        LocalDateTime createdAt,
        ServiceRequestStatus status,
        String userDepartmentName,
        String targetDepartmentName
) {
}
