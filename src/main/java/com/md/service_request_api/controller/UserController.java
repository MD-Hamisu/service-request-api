package com.md.service_request_api.controller;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.dtos.response.UserResponse;
import com.md.service_request_api.service.SuperAdminService;
import com.md.service_request_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Operation(summary = "Assign role to user", description = "Allows a super admin to assign a role to a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role or request")
    })
    @PutMapping("/{id}/assign-role")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        return ResponseEntity.ok(superAdminService.assignRole(id, role));
    }

    @Operation(summary = "Get all roles", description = "Returns a list of all possible role values excluding SUPER_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of roles retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(type = "string", example = "REQUESTER, HOD, SUPERVISOR")))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = Arrays.stream(Role.values())
                .filter(role -> role != Role.SUPER_ADMIN)
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
