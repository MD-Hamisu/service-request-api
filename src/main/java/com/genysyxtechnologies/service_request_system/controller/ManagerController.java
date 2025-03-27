package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.request.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.DashboardResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceOfferingResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceRequestResponse;
import com.genysyxtechnologies.service_request_system.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
@Tag(name = "Manager API", description = "Endpoints for manager operations in the Service Request System")
public class ManagerController {

    private final ManagerService managerService;

    @Operation(summary = "Get dashboard statistics", description = "Retrieves statistics for the manager dashboard")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(managerService.getDashboardStats());
    }

    @Operation(summary = "Get all services", description = "Retrieves a paginated list of services with filters")
    @ApiResponse(responseCode = "200", description = "List of services retrieved successfully")
    @GetMapping("/service-offerings")
    public ResponseEntity<Page<ServiceOfferingResponse>> getAllServices(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(managerService.getAllServices(name, categoryId, isActive, pageable));
    }

    @Operation(summary = "Create a new service", description = "Allows a manager to define a new service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/service-offering")
    public ResponseEntity<ServiceOfferingResponse> createService(@Valid @RequestBody ServiceOfferingDTO serviceDTO) {
        return ResponseEntity.ok(managerService.createService(serviceDTO));
    }

    @Operation(summary = "Update an existing service", description = "Allows a manager to update a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PutMapping("/service-offering/{id}")
    public ResponseEntity<ServiceOfferingResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceOfferingDTO serviceDTO
    ) {
        return ResponseEntity.ok(managerService.updateService(id, serviceDTO));
    }

    @Operation(summary = "Delete a service", description = "Allows a manager to delete a service by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @DeleteMapping("/service-offering/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        managerService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(managerService.getAllCategories());
    }

    @Operation(summary = "Create a new category", description = "Allows a manager to define a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(managerService.createCategory(categoryDTO));
    }

    @Operation(summary = "Get all requests", description = "Retrieves a paginated list of service requests with filters")
    @ApiResponse(responseCode = "200", description = "List of requests retrieved successfully")
    @GetMapping("/requests")
    public ResponseEntity<Page<ServiceRequestResponse>> getAllRequests(
            @RequestParam(value = "status", required = false) ServiceRequestStatus status,
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(managerService.getAllRequests(status, search, pageable));
    }

    @Operation(summary = "Get request details", description = "Retrieves details of a specific service request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @GetMapping("/requests/{id}")
    public ResponseEntity<ServiceRequestResponse> getRequestDetails(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getRequestDetails(id));
    }

    @Operation(summary = "Update request status", description = "Allows a manager to update the status of a request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/requests/{id}/status")
    public ResponseEntity<ServiceRequestResponse> updateRequestStatus(
            @PathVariable Long id,
            @RequestParam ServiceRequestStatus status
    ) {
        return ResponseEntity.ok(managerService.updateRequestStatus(id, status));
    }

    @Operation(summary = "Get all request statuses", description = "Retrieves a list of all possible request statuses")
    @ApiResponse(responseCode = "200", description = "List of request statuses retrieved successfully")
    @GetMapping("/request-statuses")
    public ResponseEntity<List<String>> getAllRequestStatuses() {
        return ResponseEntity.ok(managerService.getAllRequestStatuses());
    }
}
