package com.md.service_request_api.service;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.dtos.response.SuperAdminDashboardResponse;
import com.md.service_request_api.dtos.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuperAdminService {
    SuperAdminDashboardResponse getDashboardStats();
    Page<UserResponse> getAllManagers(String search, Pageable pageable);

    /*UserResponse createManager(UserDTO userDTO);
    UserResponse updateManager(Long id, UserDTO userDTO);
    void deleteManager(Long id);*/
    Page<UserResponse> getAllRequesters(String search, Pageable pageable);

    UserResponse assignRole(Long id, Role role);
}
