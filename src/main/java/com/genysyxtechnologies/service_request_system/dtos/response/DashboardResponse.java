package com.genysyxtechnologies.service_request_system.dtos.response;

public record DashboardResponse(
        long totalRequests,
        long pending,
        long inProgress,
        long completed
) {}
