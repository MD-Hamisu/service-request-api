package com.genysyxtechnologies.service_request_system.dtos.response;

public record UserResponse(
    Long id,
    String username,
    String email, String firstName, String lastName) {}
