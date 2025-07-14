package com.md.service_request_api.dtos.response;

public record DashboardResponse(
        long totalRequests,
        long pending,
        long underReview,
        long rejected,
        long inProgress,
        long completed
) {}
