package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void createNotification(Long requestId, Long userId, ServiceRequestStatus status);
    Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable);
    Page<Notification> getUnreadNotificationsByUserId(Long userId, Pageable pageable);
    void markAsRead(Long notificationId);
}
