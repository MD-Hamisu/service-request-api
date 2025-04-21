package com.genysyxtechnologies.service_request_system.dtos.response;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;

import java.time.LocalDateTime;

public record SupervisorServiceRequestDTO(
        Long id,
        LocalDateTime createdAt,
        ServiceRequestStatus status,
        String userDepartmentName,
        String targetDepartmentName
) {
}
