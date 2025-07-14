package com.md.service_request_api.service;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createNotification(Long requestId, Long userId, ServiceRequestStatus status);
    Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable);
    Page<Notification> getUnreadNotificationsByUserId(Long userId, Pageable pageable);
    void markAsRead(Long notificationId);
}
