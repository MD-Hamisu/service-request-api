package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email String email
) {}
