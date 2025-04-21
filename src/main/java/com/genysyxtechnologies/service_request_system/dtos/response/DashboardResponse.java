package com.genysyxtechnologies.service_request_system.dtos.response;

public record DashboardResponse(
        long totalRequests,
        long pending,
        long underReview,
        long rejected,
        long inProgress,
        long completed
) {}
