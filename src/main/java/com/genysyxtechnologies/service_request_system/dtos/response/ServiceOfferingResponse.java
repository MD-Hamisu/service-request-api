package com.genysyxtechnologies.service_request_system.dtos.response;

public record ServiceOfferingResponse(
        Long id,
        String name,
        String description,
        String categoryName,
        Long categoryId,
        Long departmentId,
        String departmentName,
        String fieldSchema,
        boolean isActive
) {}
