package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.User;

public interface EmailService {
    void sendRequestSubmissionEmail(User requester, ServiceRequest request);
    void sendRequestStatusChangeEmail(User requester, ServiceRequest request);
    void sendPasswordResetEmail(User user, String newPassword);
    void sendManagerAccountCreatedEmail(User manager, String initialPassword);

    void sendRoleAssignedEmail(User updatedUser, Role role);
}
