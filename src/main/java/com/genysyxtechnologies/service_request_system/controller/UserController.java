package com.genysyxtechnologies.service_request_system.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genysyxtechnologies.service_request_system.dtos.request.UserDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.UserResponse;
import com.genysyxtechnologies.service_request_system.service.SuperAdminService;
import com.genysyxtechnologies.service_request_system.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

    private final SuperAdminService superAdminService;
    private final UserService userService;

    @Operation(summary = "Get all managers", description = "Retrieves a paginated list of managers with search filter")
    @ApiResponse(responseCode = "200", description = "List of managers retrieved successfully")
    @GetMapping("/managers")
        public ResponseEntity<Page<UserResponse>> getAllManagers(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(superAdminService.getAllManagers(search, pageable));
    }

    /*@Operation(summary = "Create a new manager", description = "Allows a super admin to create a new manager")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Manager created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/managers")
        public ResponseEntity<UserResponse> createManager(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(superAdminService.createManager(userDTO));
    }

    @Operation(summary = "Update a manager", description = "Allows a super admin to update a manager's details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Manager updated successfully"),
            @ApiResponse(responseCode = "404", description = "Manager not found")
    })
    @PutMapping("/managers/{id}")
        public ResponseEntity<UserResponse> updateManager(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO
    ) {
        return ResponseEntity.ok(superAdminService.updateManager(id, userDTO));
    }

    @Operation(summary = "Delete a manager", description = "Allows a super admin to delete a manager")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Manager deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Manager not found")
    })
    @DeleteMapping("/managers/{id}")
        public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        superAdminService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }*/

    @Operation(summary = "Get all requesters", description = "Retrieves a paginated list of requesters with search filter")
    @ApiResponse(responseCode = "200", description = "List of requesters retrieved successfully")
    @GetMapping("/requesters")
        public ResponseEntity<Page<UserResponse>> getAllRequesters(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(superAdminService.getAllRequesters(search, pageable));
    }

    @Operation(summary = "Reset user password", description = "Allows a super admin to reset the password of any user to a default value (1-8)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/reset-password")
        public ResponseEntity<String> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok("Password reset to 1-8 successfully");
    }
}
