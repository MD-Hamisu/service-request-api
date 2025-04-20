package com.genysyxtechnologies.service_request_system.dtos.response;

public record ServiceRequestResponse(
        Long id,
        String serviceName,
        String userName,
        String userDepartmentName,
        String targetDepartmentName,
        String submissionDate,
        String status,
        String submittedData,
        String attachmentUrl
) {}
