package com.md.service_request_api.dtos.security;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record LoginRequest(
        @NotBlank(message = "Username or email required") String identifier,
        @NotBlank(message = "Password required") String password
) implements Serializable {}
