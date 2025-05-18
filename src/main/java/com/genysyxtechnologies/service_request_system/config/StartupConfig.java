package com.genysyxtechnologies.service_request_system.config;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class StartupConfig {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void initializeSuperAdmin() {
        if (repository.findAll().isEmpty()) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("Admin123!"));
            superAdmin.setEmail("superadmin@srs.com");
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.getRoles().addAll(List.of(Role.SUPER_ADMIN, Role.MANAGER, Role.SUPERVISOR));
            repository.save(superAdmin);
        }
    }
}
