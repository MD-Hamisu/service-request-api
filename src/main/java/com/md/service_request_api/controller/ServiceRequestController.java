package com.md.service_request_api.controller;

import java.util.List;

import com.md.service_request_api.dtos.response.SupervisorServiceRequestDTO;
import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.dtos.request.UpdateStatusDto;
import com.md.service_request_api.dtos.response.ServiceRequestResponse;
import com.md.service_request_api.service.ManagerService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Tag(name = "Service Request API", description = "Endpoints for managing service requests")
public class ServiceRequestController {

    private final ManagerService managerService;

    @Operation(summary = "Get all requests", description = "Retrieves a paginated list of service requests with filters")
    @ApiResponse(responseCode = "200", description = "List of requests retrieved successfully")
    @GetMapping
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
    @GetMapping("/{id}")
        public ResponseEntity<ServiceRequestResponse> getRequestDetails(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getRequestDetails(id));
    }

    @Operation(summary = "Update request status", description = "Allows a manager to update the status of a request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/{id}/status")
        public ResponseEntity<ServiceRequestResponse> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusDto dto
    ) {
        return ResponseEntity.ok(managerService.updateRequestStatus(id, dto));
    }

    @Operation(summary = "Get all request statuses", description = "Retrieves a list of all possible request statuses")
    @ApiResponse(responseCode = "200", description = "List of request statuses retrieved successfully")
    @GetMapping("/statuses")
        public ResponseEntity<List<String>> getAllRequestStatuses() {
        return ResponseEntity.ok(managerService.getAllRequestStatuses());
    }

    @Operation(summary = "Get all service requests for supervisor",
            description = "Retrieves a paginated list of all service requests, filterable by user department and/or target department. Page numbers are 1-based (e.g., page=1 for the first page).")
    @ApiResponse(responseCode = "200", description = "List of service requests retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied (Supervisor role required)")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @GetMapping("/supervisor")
    public ResponseEntity<Page<SupervisorServiceRequestDTO>> getAllRequests(
            @RequestParam(value = "userDepartmentId", required = false) Long userDepartmentId,
            @RequestParam(value = "targetDepartmentId", required = false) Long targetDepartmentId,
            @RequestParam(value = "serviceRequestStatus", required = false) ServiceRequestStatus serviceRequestStatus,
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(value = 1) Integer size
    ) {
        Page<SupervisorServiceRequestDTO> requests = managerService.getRequestsForSupervisor(
                userDepartmentId, targetDepartmentId, serviceRequestStatus, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(requests);
    }
}
