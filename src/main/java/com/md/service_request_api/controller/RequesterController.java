package com.md.service_request_api.controller;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.dtos.request.SubmitRequestDTO;
import com.md.service_request_api.dtos.response.CategoryResponse;
import com.md.service_request_api.dtos.response.DepartmentResponse;
import com.md.service_request_api.dtos.response.ServiceOfferingResponse;
import com.md.service_request_api.dtos.response.ServiceRequestResponse;
import com.md.service_request_api.service.DepartmentService;
import com.md.service_request_api.service.RequesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/requester")
@PreAuthorize("hasAuthority('REQUESTER')")
@RequiredArgsConstructor
@Tag(name = "Requester API", description = "Endpoints for requester operations in the Service Request System")
public class RequesterController {

    private final RequesterService requesterService;
    private final DepartmentService departmentService;

    @Operation(summary = "Get available services", description = "Retrieves a list of active services available to requesters, filtered by name, category, and department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of services retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/home")
    public ResponseEntity<Page<ServiceOfferingResponse>> getAvailableServices(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(requesterService.getAvailableServices(name, categoryId, departmentId, pageable));
    }

    @Operation(summary = "Get all departments", description = "Retrieves a list of all departments for filtering services")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved successfully")
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories for filtering services")
    @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(requesterService.getCategories());
    }

    @Operation(summary = "Get service for request", description = "Retrieves details of a specific service, including its form template, to render the request form")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @GetMapping("/request/{serviceId}")
    public ResponseEntity<ServiceOfferingResponse> getServiceForRequestForm(@PathVariable Long serviceId) {
        return ResponseEntity.ok(requesterService.getServiceForRequestForm(serviceId));
    }

    @Operation(summary = "Submit a service request", description = "Allows a requester to submit a new service request with form data and an optional attachment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PostMapping(value = "/request/{serviceId}")
    public ResponseEntity<?> submitRequest(
            @PathVariable Long serviceId,
            @RequestBody @Validated SubmitRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(requesterService.submitRequest(
                serviceId,
                requestDTO.requestData(),
                requestDTO.userDepartmentId(),
                null
        ));
    }

    @Operation(summary = "Get user requests", description = "Retrieves a paginated list of requests submitted by the authenticated user, filtered by status and search term")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of user requests retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters")
    })
    @GetMapping("/requests")
    public ResponseEntity<Page<ServiceRequestResponse>> getUserRequests(
            @RequestParam(value = "status", required = false) ServiceRequestStatus status,
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(requesterService.getUserRequests(status, search, pageable));
    }

    @Operation(summary = "Get all service request statuses", description = "Retrieves a list of all possible service request statuses for filtering requests")
    @ApiResponse(responseCode = "200", description = "List of statuses retrieved successfully")
    @GetMapping("/statuses")
    public ResponseEntity<List<ServiceRequestStatus>> getServiceRequestStatuses() {
        List<ServiceRequestStatus> statuses = Arrays.stream(ServiceRequestStatus.values())
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }
}
