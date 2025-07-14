package com.md.service_request_api.controller;

import com.md.service_request_api.dtos.response.DashboardResponse;
import com.md.service_request_api.dtos.response.SuperAdminDashboardResponse;
import com.md.service_request_api.service.ManagerService;
import com.md.service_request_api.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard API", description = "Endpoints for dashboard statistics")
public class DashboardController {

    private final ManagerService managerService;
    private final SuperAdminService superAdminService;

    @Operation(summary = "Get manager dashboard statistics", description = "Retrieves statistics for the manager dashboard")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully")
    @GetMapping("/manager")
        public ResponseEntity<DashboardResponse> getManagerDashboardStats() {
        return ResponseEntity.ok(managerService.getDashboardStats());
    }

    @Operation(summary = "Get super admin dashboard statistics", description = "Retrieves statistics for the super admin dashboard")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully")
    @GetMapping("/super-admin")
        public ResponseEntity<SuperAdminDashboardResponse> getSuperAdminDashboardStats() {
        return ResponseEntity.ok(superAdminService.getDashboardStats());
    }
}
