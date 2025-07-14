package com.md.service_request_api.service.impl;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.model.Notification;
import com.md.service_request_api.repository.NotificationRepository;
import com.md.service_request_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    /**
     * Creates a notification for a user when a service request's status changes.
     *
     * @param requestId the ID of the service request
     * @param userId    the ID of the user (requester) to notify
     * @param status    the new status of the request
     */
    @Override
    public void createNotification(Long requestId, Long userId, ServiceRequestStatus status) {
        Notification notification = new Notification();
        notification.setRequestId(requestId);
        notification.setUserId(userId);
        notification.setStatus(status);
        notification.setMessage(String.format("Your request #%d is now %s: %s",
                requestId, status.name(), status.getDescription()));
        notificationRepository.save(notification);
    }

    /**
     * Retrieves all notifications for a user, ordered by creation time (newest first).
     *
     * @param userId the ID of the user
     * @return list of notifications
     */
    @Override
    public Page<Notification> getNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Retrieves all unread notifications for a user, ordered by creation time (newest first).
     *
     * @param userId the ID of the user
     * @return list of unread notifications
     */
    @Override
    public Page<Notification> getUnreadNotificationsByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadIsFalseOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId the ID of the notification to mark as read
     * @throws IllegalArgumentException if the notification is not found
     */
    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
