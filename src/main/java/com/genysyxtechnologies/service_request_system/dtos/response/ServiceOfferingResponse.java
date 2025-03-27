package com.genysyxtechnologies.service_request_system.dtos.response;

public record ServiceOfferingResponse(
        Long id,
        String name,
        String description,
        String categoryName,
        String formTemplate,
        boolean isActive
) {}
