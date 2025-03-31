package com.genysyxtechnologies.service_request_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.service.ManagerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "Endpoints for managing service categories")
public class CategoryController {

    private final ManagerService managerService;

    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories")
    @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(managerService.getAllCategories());
    }

    @Operation(summary = "Create a new category", description = "Allows a manager to define a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(managerService.createCategory(categoryDTO));
    }

    @Operation(summary = "Update a category", description = "Allows a manager to update an existing category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        return ResponseEntity.ok(managerService.updateCategory(id, categoryDTO));
    }
} 