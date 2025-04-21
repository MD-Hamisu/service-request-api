package com.genysyxtechnologies.service_request_system.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ServiceRequestResponse(
        Long id,
        String serviceName,
        String userName,
        String userDepartmentName,
        String targetDepartmentName,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime submissionDate,
        String status,
        String submittedData,
        String attachmentUrl,
        String rejectionReason
) {}
