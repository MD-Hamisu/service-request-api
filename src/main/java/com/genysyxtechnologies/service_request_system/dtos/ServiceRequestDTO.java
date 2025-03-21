package com.genysyxtechnologies.service_request_system.dtos;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRequestDTO {
    @NotNull(message = "User ID cannot be null")
    @Schema(description = "ID of the user submitting the request", example = "1", required = true)
    private Long userId;

    @NotNull(message = "Service offering ID cannot be null")
    @Schema(description = "ID of the service being requested", example = "1", required = true)
    private Long serviceOfferingId; // Updated field name

    @NotBlank(message = "Status cannot be blank")
    @Schema(description = "Status of the request", example = "PENDING", required = true)
    private ServiceRequestStatus status;

    @NotBlank(message = "Fields data cannot be blank")
    @Schema(description = "JSON string of submitted field data", example = "{\"issue\": \"PC crashed\"}", required = true)
    private String fieldsData;
}
