package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.dtos.request.ChangePasswordRequest;

public interface UserService {
    void changePassword(ChangePasswordRequest request);
    void resetPassword(Long userId);
}
