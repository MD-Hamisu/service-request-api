package com.genysyxtechnologies.service_request_system.dtos;

import lombok.Data;

@Data
public class ServiceOfferingDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String fields; // JSON string
    private boolean isActive;
}
