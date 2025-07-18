package com.md.service_request_api.controller;

import com.md.service_request_api.dtos.request.ServiceOfferingDTO;
import com.md.service_request_api.dtos.response.ServiceOfferingResponse;
import com.md.service_request_api.service.ManagerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Service API", description = "Endpoints for managing service offerings")
public class ServiceController {

    private final ManagerService managerService;

    @Operation(summary = "Get all services", description = "Retrieves a paginated list of services with filters")
    @ApiResponse(responseCode = "200", description = "List of services retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<ServiceOfferingResponse>> getAllServices(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam("page") @Min(value = 1) Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(managerService.getAllServices(name, categoryId, departmentId, isActive, PageRequest.of(page - 1, size)));
    }

    @Operation(summary = "Create a new service", description = "Allows a manager to define a new service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
        public ResponseEntity<ServiceOfferingResponse> createService(@Validated @RequestBody ServiceOfferingDTO serviceDTO) {
        return ResponseEntity.ok(managerService.createService(serviceDTO));
    }

    @Operation(summary = "Update an existing service", description = "Allows a manager to update a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PutMapping("/{id}")
        public ResponseEntity<ServiceOfferingResponse> updateService(
            @PathVariable Long id,
            @Validated @RequestBody ServiceOfferingDTO serviceDTO
    ) {
        return ResponseEntity.ok(managerService.updateService(id, serviceDTO));
    }

    @Operation(summary = "Delete a service", description = "Allows a manager to delete a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        managerService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
