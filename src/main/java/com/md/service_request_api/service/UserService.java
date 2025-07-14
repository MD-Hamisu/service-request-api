package com.md.service_request_api.service;

import com.md.service_request_api.dtos.request.ChangePasswordRequest;

public interface UserService {
    void changePassword(ChangePasswordRequest request);
    void resetPassword(Long userId);
    void synchronizeUsers();
}
