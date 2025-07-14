package com.md.service_request_api.dtos.response;

public record DepartmentResponse(
        Long id,
        String name,
        String code,
        String hod
) {}
