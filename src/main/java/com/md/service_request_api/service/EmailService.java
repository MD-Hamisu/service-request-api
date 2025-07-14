package com.md.service_request_api.service;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.model.ServiceRequest;
import com.md.service_request_api.model.User;

public interface EmailService {
    void sendRequestSubmissionEmail(User requester, ServiceRequest request);
    void sendRequestStatusChangeEmail(User requester, ServiceRequest request);
    void sendPasswordResetEmail(User user, String newPassword);
    void sendManagerAccountCreatedEmail(User manager, String initialPassword);

    void sendRoleAssignedEmail(User updatedUser, Role role);
}
