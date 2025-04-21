package com.genysyxtechnologies.service_request_system.controller;

import com.genysyxtechnologies.service_request_system.model.Notification;
import com.genysyxtechnologies.service_request_system.service.NotificationService;
import com.genysyxtechnologies.service_request_system.service.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @Operation(summary = "Get all notifications for a user", description = "Retrieves all notifications for a specific user, ordered by creation time (newest first)")
    @ApiResponse(responseCode = "200", description = "List of notifications retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Notification>> getNotificationsByUserId(@PathVariable Long userId,
                                                                       @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId, pageable));
    }

    @Operation(summary = "Get all notifications for a self", description = "Retrieves all notifications for a specific logged in user, ordered by creation time (newest first)")
    @ApiResponse(responseCode = "200", description = "List of notifications retrieved successfully")
    @GetMapping("/user/me")
    public ResponseEntity<Page<Notification>> getNotificationsForCurrentUser(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Long userId = securityUtil.getCurrentUser().getId();
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId, pageable));
    }

    @Operation(summary = "Get unread notifications for a user", description = "Retrieves all unread notifications for a specific user, ordered by creation time (newest first)")
    @ApiResponse(responseCode = "200", description = "List of unread notifications retrieved successfully")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<Page<Notification>> getUnreadNotificationsByUserId(@PathVariable Long userId,
                                                                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId, pageable));
    }

    @Operation(summary = "Mark a notification as read", description = "Marks a specific notification as read")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
