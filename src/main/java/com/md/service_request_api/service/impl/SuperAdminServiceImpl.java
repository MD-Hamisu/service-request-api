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

    /*@Override
    public UserResponse createManager(UserDTO userDTO) {
        // Check if username or email already exists
        if (userRepository.findByUsername(userDTO.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Create new Manager user
        User manager = new User();
        manager.setUsername(userDTO.username());
        manager.setEmail(userDTO.email());
        manager.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD)); // set default password
        manager.setRoles(Set.of(Role.MANAGER));
        User savedManager = userRepository.save(manager);

        // Send email notification to the Manager
        emailService.sendManagerAccountCreatedEmail(savedManager, DEFAULT_PASSWORD);

        return new UserResponse(savedManager.getId(), savedManager.getUsername(), savedManager.getEmail());
    }

    @Override
    public UserResponse updateManager(Long id, UserDTO userDTO) {
        User manager = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));

        // Ensure the user is a Manager
        if (!manager.getRoles().contains(Role.MANAGER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a Manager");
        }

        // Check for duplicate username or email (excluding the current user)
        userRepository.findByUsername(userDTO.username())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists"); });
        userRepository.findByEmail(userDTO.email())
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists"); });

        // Update Manager details
        manager.setUsername(userDTO.username());
        manager.setEmail(userDTO.email());
        manager.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD)); // set default password
        User updatedManager = userRepository.save(manager);

        // Send email notification to the Manager
        emailService.sendManagerAccountCreatedEmail(manager, DEFAULT_PASSWORD);

        return new UserResponse(updatedManager.getId(), updatedManager.getUsername(), updatedManager.getEmail());
    }

    @Override
    public void deleteManager(Long id) {
        User manager = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));
        if (!manager.getRoles().contains(Role.MANAGER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a Manager");
        }
        userRepository.delete(manager);
    }*/

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
