package com.genysyxtechnologies.service_request_system.dtos.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record SubmitRequestDTO(
        @NotBlank(message = "Request data is required") String requestData,
        MultipartFile attachment // Optional, no validation needed
) {}
