package com.genysyxtechnologies.service_request_system.dtos.response;

public record ServiceRequestResponse(
        Long id,
        String serviceName,
        String submittedBy,
        String submissionDate,
        String status,
        String requestData,
        String attachmentUrl
) {}
