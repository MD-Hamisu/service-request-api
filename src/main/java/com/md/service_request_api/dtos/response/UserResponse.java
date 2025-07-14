package com.md.service_request_api.dtos.response;

public record UserResponse(
    Long id,
    String username,
    String email, String firstName, String lastName) {}
