package com.genysyxtechnologies.service_request_system.dtos.response;

public record SuperAdminDashboardResponse(
        long totalRequests,
        long totalRequesters,
        long totalManagers,
        long totalServices
) {}
