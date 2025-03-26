package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.request.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.model.Category;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Manager API", description = "Endpoints for admin operations in the Service Request System")
public class ManagerController {

    private final ManagerService managerService;

    @Operation(summary = "Create a new service", description = "Allows a manager to define a new service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/service-offering")
    public ResponseEntity<ServiceOffering> createService(@Valid @RequestBody ServiceOfferingDTO serviceDTO) {
        return ResponseEntity.ok(managerService.createService(serviceDTO));
    }

    @Operation(summary = "Update an existing service-offering", description = "Allows an manager to update a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PutMapping("/service-offering/{id}")
    public ResponseEntity<ServiceOffering> updateService(@PathVariable Long id,
                                                         @Valid @RequestBody ServiceOfferingDTO serviceDTO) {
        return ResponseEntity.ok(managerService.updateService(id, serviceDTO));
    }

    @Operation(summary = "Delete a service", description = "Allows an manager to delete a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @DeleteMapping("/service-offering/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        managerService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all services", description = "Retrieves a list of all services")
    @ApiResponse(responseCode = "200", description = "List of services retrieved successfully")
    @GetMapping("/service-offerings")
    public ResponseEntity<List<ServiceOffering>> getAllServices() {
        return ResponseEntity.ok(managerService.getAllServices());
    }

    @Operation(summary = "Create a new category", description = "Allows an manager to define a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(managerService.createCategory(categoryDTO));
    }

    @Operation(summary = "Get all requests", description = "Retrieves a list of all service requests")
    @ApiResponse(responseCode = "200", description = "List of requests retrieved successfully")
    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequest>> getAllRequests(@RequestParam(required = false) ServiceRequestStatus status) {
        return ResponseEntity.ok(managerService.getAllRequests(status));
    }

    @Operation(summary = "Update request status", description = "Allows an manager to update the status of a request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/requests/status/{id}")
    public ResponseEntity<ServiceRequest> updateRequestStatus(@PathVariable Long id, @RequestParam ServiceRequestStatus status) {
        return ResponseEntity.ok(managerService.updateRequestStatus(id, status));
    }

    @Operation(summary = "Get all request statuses", description = "Retrieves a list of all possible request statuses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of request statuses retrieved successfully")
    })
    @GetMapping("/request-statuses")
    public ResponseEntity<List<String>> getAllRequestStatuses() {
        return ResponseEntity.ok(managerService.getAllRequestStatuses());
    }
}
