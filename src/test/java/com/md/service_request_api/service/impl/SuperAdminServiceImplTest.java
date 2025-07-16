/*
package com.md.service_request_api.service.impl;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.dtos.request.UserDTO;
import com.md.service_request_api.dtos.response.SuperAdminDashboardResponse;
import com.md.service_request_api.dtos.response.UserResponse;
import com.md.service_request_api.model.ServiceOffering;
import com.md.service_request_api.model.ServiceRequest;
import com.md.service_request_api.model.User;
import com.md.service_request_api.repository.ServiceOfferingRepository;
import com.md.service_request_api.repository.ServiceRequestRepository;
import com.md.service_request_api.repository.UserRepository;
import com.md.service_request_api.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuperAdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SuperAdminServiceImpl superAdminService;

    private User manager;
    private User requester;
    private ServiceRequest serviceRequest;
    private ServiceOffering serviceOffering;

    @BeforeEach
    void setUp() {
        // Initialize test data
        manager = new User();
        manager.setId(1L);
        manager.setUsername("manager1");
        manager.setEmail("manager1@example.com");
        manager.setPassword("encodedPassword");
        manager.setRoles(Set.of(Role.MANAGER));

        requester = new User();
        requester.setId(2L);
        requester.setUsername("requester1");
        requester.setEmail("requester1@example.com");
        requester.setPassword("encodedPassword");
        requester.setRoles(Set.of(Role.REQUESTER));

        serviceOffering = new ServiceOffering();
        serviceOffering.setId(1L);
        serviceOffering.setName("Laptop Repair");
        serviceOffering.setActive(true);

        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        serviceRequest.setUser(requester);
        serviceRequest.setService(serviceOffering);
    }

    @Test
    void getDashboardStats_shouldReturnStats() {
        // Arrange
        when(serviceRequestRepository.count()).thenReturn(10L);
        when(userRepository.countByRolesContaining(Role.REQUESTER)).thenReturn(5L);
        when(userRepository.countByRolesContaining(Role.MANAGER)).thenReturn(3L);
        when(serviceOfferingRepository.count()).thenReturn(8L);

        // Act
        SuperAdminDashboardResponse response = superAdminService.getDashboardStats();

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.totalRequests());
        assertEquals(5L, response.totalRequesters());
        assertEquals(3L, response.totalManagers());
        assertEquals(8L, response.totalServices());

        verify(serviceRequestRepository).count();
        verify(userRepository).countByRolesContaining(Role.REQUESTER);
        verify(userRepository).countByRolesContaining(Role.MANAGER);
        verify(serviceOfferingRepository).count();
    }

    @Test
    void getAllManagers_shouldReturnPagedManagers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username"));
        Page<User> managerPage = new PageImpl<>(List.of(manager), pageable, 1);

        when(userRepository.findByRoleWithFilters(Role.MANAGER, "manager", pageable))
                .thenReturn(managerPage);

        // Act
        Page<UserResponse> responsePage = superAdminService.getAllManagers("manager", pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        UserResponse response = responsePage.getContent().get(0);
        assertEquals(1L, response.id());
        assertEquals("manager1", response.username());
        assertEquals("manager1@example.com", response.email());

        verify(userRepository).findByRoleWithFilters(Role.MANAGER, "manager", pageable);
    }

    @Test
    void createManager_shouldCreateAndReturnManager() {
        // Arrange
        UserDTO userDTO = new UserDTO("newmanager", "newmanager@example.com");
        when(userRepository.findByUsername("newmanager")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newmanager@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(manager);

        // Act
        UserResponse response = superAdminService.createManager(userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("manager1", response.username());
        assertEquals("manager1@example.com", response.email());

        verify(userRepository).findByUsername("newmanager");
        verify(userRepository).findByEmail("newmanager@example.com");
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendManagerAccountCreatedEmail(manager, "12345678");
    }

    @Test
    void createManager_usernameExists_shouldThrowBadRequest() {
        // Arrange
        UserDTO userDTO = new UserDTO("manager1", "newmanager@example.com");
        when(userRepository.findByUsername("manager1")).thenReturn(Optional.of(manager));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.createManager(userDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Username already exists", exception.getReason());

        verify(userRepository).findByUsername("manager1");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createManager_emailExists_shouldThrowBadRequest() {
        // Arrange
        UserDTO userDTO = new UserDTO("newmanager", "manager1@example.com");
        when(userRepository.findByUsername("newmanager")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("manager1@example.com")).thenReturn(Optional.of(manager));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.createManager(userDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email already exists", exception.getReason());

        verify(userRepository).findByUsername("newmanager");
        verify(userRepository).findByEmail("manager1@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateManager_shouldUpdateAndReturnManager() {
        // Arrange
        UserDTO userDTO = new UserDTO("updatedmanager", "updatedmanager@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(userRepository.findByUsername("updatedmanager")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updatedmanager@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(manager);

        // Act
        UserResponse response = superAdminService.updateManager(1L, userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("updatedmanager", response.username());
        assertEquals("updatedmanager@example.com", response.email());

        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("updatedmanager");
        verify(userRepository).findByEmail("updatedmanager@example.com");
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendManagerAccountCreatedEmail(manager, "12345678");
    }

    @Test
    void updateManager_managerNotFound_shouldThrowNotFound() {
        // Arrange
        UserDTO userDTO = new UserDTO("updatedmanager", "updatedmanager@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.updateManager(1L, userDTO));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Manager not found", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateManager_notAManager_shouldThrowBadRequest() {
        // Arrange
        User nonManager = new User();
        nonManager.setId(1L);
        nonManager.setUsername("requester1");
        nonManager.setEmail("requester1@example.com");
        nonManager.setRoles(Set.of(Role.REQUESTER));

        UserDTO userDTO = new UserDTO("updatedmanager", "updatedmanager@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(nonManager));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.updateManager(1L, userDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User is not a Manager", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateManager_usernameExists_shouldThrowBadRequest() {
        // Arrange
        User anotherManager = new User();
        anotherManager.setId(2L);
        anotherManager.setUsername("anothermanager");
        anotherManager.setEmail("anothermanager@example.com");
        anotherManager.setRoles(Set.of(Role.MANAGER));

        UserDTO userDTO = new UserDTO("anothermanager", "updatedmanager@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(userRepository.findByUsername("anothermanager")).thenReturn(Optional.of(anotherManager));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.updateManager(1L, userDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Username already exists", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("anothermanager");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateManager_emailExists_shouldThrowBadRequest() {
        // Arrange
        User anotherManager = new User();
        anotherManager.setId(2L);
        anotherManager.setUsername("anothermanager");
        anotherManager.setEmail("anothermanager@example.com");
        anotherManager.setRoles(Set.of(Role.MANAGER));

        UserDTO userDTO = new UserDTO("updatedmanager", "anothermanager@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(userRepository.findByUsername("updatedmanager")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("anothermanager@example.com")).thenReturn(Optional.of(anotherManager));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.updateManager(1L, userDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email already exists", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("updatedmanager");
        verify(userRepository).findByEmail("anothermanager@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteManager_shouldDeleteManager() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));

        // Act
        superAdminService.deleteManager(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(manager);
    }

    @Test
    void deleteManager_managerNotFound_shouldThrowNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.deleteManager(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Manager not found", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteManager_notAManager_shouldThrowBadRequest() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> superAdminService.deleteManager(1L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User is not a Manager", exception.getReason());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void getAllRequesters_shouldReturnPagedRequesters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username"));
        Page<User> requesterPage = new PageImpl<>(List.of(requester), pageable, 1);

        when(userRepository.findByRoleWithFilters(Role.REQUESTER, "requester", pageable))
                .thenReturn(requesterPage);

        // Act
        Page<UserResponse> responsePage = superAdminService.getAllRequesters("requester", pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        UserResponse response = responsePage.getContent().get(0);
        assertEquals(2L, response.id());
        assertEquals("requester1", response.username());
        assertEquals("requester1@example.com", response.email());

        verify(userRepository).findByRoleWithFilters(Role.REQUESTER, "requester", pageable);
    }
}
*/
