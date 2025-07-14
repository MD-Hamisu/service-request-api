package com.md.service_request_api.dtos.security;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record SignupRequest(
        @NotBlank(message = "Username required") String username,
        @NotBlank(message = "Password required") String password,
        @NotBlank(message = "Email required") String email,
        @NotBlank(message = "First name required") String firstName,
        @NotBlank(message = "Last name required") String lastName
) implements Serializable {}
