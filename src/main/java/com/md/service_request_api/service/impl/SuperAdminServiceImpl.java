package com.md.service_request_api.service.impl;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.dtos.response.SuperAdminDashboardResponse;
import com.md.service_request_api.dtos.response.UserResponse;
import com.md.service_request_api.model.User;
import com.md.service_request_api.repository.ServiceOfferingRepository;
import com.md.service_request_api.repository.ServiceRequestRepository;
import com.md.service_request_api.repository.UserRepository;
import com.md.service_request_api.service.EmailService;
import com.md.service_request_api.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final EmailService emailService;

    @Override
    public SuperAdminDashboardResponse getDashboardStats() {
        long totalRequests = serviceRequestRepository.count();
        long totalRequesters = userRepository.countByRolesContaining(Role.REQUESTER);
        long totalManagers = userRepository.countByRolesContaining(Role.HOD);
        long totalServices = serviceOfferingRepository.count();
        return new SuperAdminDashboardResponse(totalRequests, totalRequesters, totalManagers, totalServices);
    }

    @Override
    public Page<UserResponse> getAllManagers(String search, Pageable pageable) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search : null;
        Page<User> managers = userRepository.findByRolesWithFilters(Set.of(Role.HOD, Role.SUPERVISOR), searchTerm, pageable);
        return managers.map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()));
    }


    @Override
    public Page<UserResponse> getAllRequesters(String search, Pageable pageable) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search : null;
        Page<User> requesters = userRepository.findByRoleWithFilters(Role.REQUESTER, searchTerm, pageable);
        return requesters.map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()));
    }

    @Override
    public UserResponse assignRole(Long id, Role role) {
        // Validate role
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot be null");
        }

        // Find user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update user roles
        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(role);
        user.setRoles(roles);

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Send email notification
        emailService.sendRoleAssignedEmail(updatedUser, role);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName()
        );
    }
}
