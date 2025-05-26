package com.genysyxtechnologies.service_request_system.service.impl;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.dtos.request.UserDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.SuperAdminDashboardResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.UserResponse;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.repository.UserRepository;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import com.genysyxtechnologies.service_request_system.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final String DEFAULT_PASSWORD = "12345678";

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
        Page<User> managers = userRepository.findByRoleWithFilters(Role.HOD, searchTerm, pageable);
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
}
