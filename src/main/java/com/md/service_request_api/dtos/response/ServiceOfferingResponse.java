package com.md.service_request_api.dtos.response;

public record ServiceOfferingResponse(
        Long id,
        String name,
        String description,
        String categoryName,
        Long categoryId,
        Long departmentId,
        String departmentName,
        boolean isActive
) {}
