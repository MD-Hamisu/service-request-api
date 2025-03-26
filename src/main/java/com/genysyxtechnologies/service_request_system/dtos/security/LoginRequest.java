package com.genysyxtechnologies.service_request_system.dtos.security;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public record LoginRequest(
        @NotBlank(message = "Username or email required") String identifier,
        @NotBlank(message = "Password required") String password
) implements Serializable {}
