package com.genysyxtechnologies.service_request_system.service.impl;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.dtos.request.ChangePasswordRequest;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.DepartmentRepository;
import com.genysyxtechnologies.service_request_system.repository.UserRepository;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import com.genysyxtechnologies.service_request_system.service.UserService;
import com.genysyxtechnologies.service_request_system.service.util.SecurityUtil;
import com.genysyxtechnologies.service_request_system.service.util.sync.APIKeyService;
import com.genysyxtechnologies.service_request_system.service.util.sync.NetworkRequest;
import com.genysyxtechnologies.service_request_system.service.util.sync.URLConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final EmailService emailService;
    private final NetworkRequest<String, JSONArray> networkRequest;
    private final APIKeyService apiKeyService;
    private final Function<String, JSONArray> conv = (s) -> {
        if(s.isBlank())
            return new JSONArray();
        return new JSONArray(s);
    };

    private static final String DEFAULT_PASSWORD = "12345678";
    private final DepartmentRepository departmentRepository;

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = securityUtil.getCurrentUser();
        // Verify the old password
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Reset the password to the default value
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userRepository.save(user);
        // Send email notification to the user
        emailService.sendPasswordResetEmail(user, DEFAULT_PASSWORD);
    }

    @Async
    @Transactional
    @Override
    public void synchronizeUsers() {
        // Make network call to fetch users
        var resp = networkRequest.makeRequest(
                URLConstants.GET_USERS,
                new HashMap<>(),
                HttpResponse.BodyHandlers.ofString(),
                conv,
                () -> {},
                apiKeyService
        );

        // Process user data
        for (int i = 0; i < resp.length(); i++) {
            var jsonObject = resp.getJSONObject(i);
            String username = jsonObject.getString("username");

            // Check if user already exists
            User user = userRepository.findByUsername(username)
                    .orElse(new User());

            // Update user fields
            user.setUsername(username);
            user.setEmail(jsonObject.getString("email"));
            user.setFirstName(jsonObject.getString("firstName"));
            user.setLastName(jsonObject.getString("lastName"));

            // Set default password if new user
            if (user.getId() == null) {
                String tempPassword = UUID.randomUUID().toString().substring(0, 12);
                user.setPassword(passwordEncoder.encode(tempPassword));
                emailService.sendPasswordResetEmail(user, tempPassword);
            }

            // Handle roles
            Set<Role> roles = new HashSet<>();
            JSONArray rolesArray = jsonObject.getJSONArray("roles");
            for (int j = 0; j < rolesArray.length(); j++) {
                String roleName = rolesArray.getString(j).toUpperCase();
                try {
                    roles.add(Role.valueOf(roleName));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role {} for user {}", roleName, username);
                }
            }
            // assign requester role to all Users
            roles.add(Role.REQUESTER);
            user.setRoles(roles);

            // Handle department and HOD role
            if (jsonObject.has("departmentId")) {
                Long departmentId = jsonObject.getLong("departmentId");
                departmentRepository.findById(departmentId).ifPresent(department -> {
                    user.setDepartment(department);
                    if (roles.contains(Role.HOD)) {
                        // Update department's HODUser
                        department.setHODUser(user);
                        // Remove HOD role from existing HOD if different
                        userRepository.findByRolesContainingAndDepartmentId(Role.HOD, departmentId)
                                .filter(existingHod -> !existingHod.getId().equals(user.getId()))
                                .ifPresent(existingHod -> {
                                    existingHod.getRoles().remove(Role.HOD);
                                    userRepository.save(existingHod);
                                    // Clear HODUser if the existing HOD is being replaced
                                    if (department.getHODUser() != null && department.getHODUser().getId().equals(existingHod.getId())) {
                                        department.setHODUser(null);
                                    }
                                });
                        // Save department with updated HODUser
                        departmentRepository.save(department);
                    }
                });
            }

            userRepository.save(user);
        }
    }
}
