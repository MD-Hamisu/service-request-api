package com.md.service_request_api.dtos.response;

public record SuperAdminDashboardResponse(
        long totalRequests,
        long totalRequesters,
        long totalManagers,
        long totalServices
) {}
