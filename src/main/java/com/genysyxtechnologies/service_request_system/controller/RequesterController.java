package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.ServiceRequestDTO;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.service.RequesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requester")
@PreAuthorize("hasRole('REQUESTER')")
@RequiredArgsConstructor
@Tag(name = "Requester API", description = "Endpoints for requester operations in the Service Request System")
public class RequesterController {

    private final RequesterService requesterService;

    @Operation(summary = "Get available services", description = "Retrieves a list of active services available to requesters")
    @ApiResponse(responseCode = "200", description = "List of services retrieved successfully")
    @GetMapping("/service-offering")
    public ResponseEntity<Page<ServiceOffering>> getAvailableServices(@RequestParam String name,
                                                                      @RequestParam Pageable pageable) {
        return ResponseEntity.ok(requesterService.getAvailableServices(name,pageable));
    }

    @Operation(summary = "Submit a service request", description = "Allows a requester to submit a new service request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/requests")
    public ResponseEntity<ServiceRequest> submitRequest(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        return ResponseEntity.ok(requesterService.submitRequest(serviceRequestDTO));
    }

    @Operation(summary = "Get user requests", description = "Retrieves a list of requests submitted by a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of user requests retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequest>> getUserRequests(@RequestParam Long userId, @RequestParam(required = false) ServiceRequestStatus requestStatus) {
        return ResponseEntity.ok(requesterService.getUserRequests(userId, requestStatus));
    }
}