package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.dtos.response.DepartmentResponse;
import com.genysyxtechnologies.service_request_system.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department API", description = "Endpoints for managing departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @Operation(summary = "Get all departments", description = "Retrieves a list of all departments")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved successfully")
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(summary = "Get department by ID", description = "Retrieves a department by its ID")
    @ApiResponse(responseCode = "200", description = "Department retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @Operation(summary = "Search departments by name or code", description = "Searches for departments whose name or code matches the query string")
    @ApiResponse(responseCode = "200", description = "List of matching departments retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<List<DepartmentResponse>> findAllByNameOrCode(@RequestParam("query") String query) {
        if (query.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        var data = departmentService.findByQuery(query);
        return ResponseEntity.ok(data);
    }

    @Operation(summary = "Synchronize departments", description = "Triggers synchronization of departments with an external system")
    @ApiResponse(responseCode = "200", description = "Departments synchronized successfully")
    @ApiResponse(responseCode = "500", description = "Synchronization failed due to an error (e.g., network issue)")
    @PostMapping("/synchronize")
    public ResponseEntity<Void> synchronizeDepartments() {
        departmentService.synchronizeDepartments();
        return ResponseEntity.accepted().build();
    }
}
