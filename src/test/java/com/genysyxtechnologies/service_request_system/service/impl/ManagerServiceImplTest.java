package com.genysyxtechnologies.service_request_system.service.impl;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.request.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.DashboardResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceOfferingResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceRequestResponse;
import com.genysyxtechnologies.service_request_system.model.Category;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.CategoryRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private Category category;
    private ServiceOffering serviceOffering;
    private ServiceRequest serviceRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize test data
        category = new Category();
        category.setId(1L);
        category.setName("IT Services");

        serviceOffering = new ServiceOffering();
        serviceOffering.setId(1L);
        serviceOffering.setName("Laptop Repair");
        serviceOffering.setDescription("Repair services for laptops");
        serviceOffering.setCategory(category);
        serviceOffering.setFieldSchema("{\"issue\": \"string\"}");
        serviceOffering.setActive(true);

        user = new User();
        user.setId(1L);
        user.setUsername("requester1");
        user.setEmail("requester1@example.com");
        user.setRoles(Set.of(Role.REQUESTER));

        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        serviceRequest.setService(serviceOffering);
        serviceRequest.setUser(user);
        serviceRequest.setSubmissionDate(LocalDateTime.now());
        serviceRequest.setStatus(ServiceRequestStatus.PENDING);
        serviceRequest.setSubmittedData("{\"issue\": \"screen broken\"}");
        serviceRequest.setAttachmentUrl("http://example.com/attachment.pdf");
    }

    @Test
    void getDashboardStats_shouldReturnStats() {
        // Arrange
        when(serviceRequestRepository.count()).thenReturn(10L);
        when(serviceRequestRepository.countByStatus(ServiceRequestStatus.PENDING)).thenReturn(4L);
        when(serviceRequestRepository.countByStatus(ServiceRequestStatus.IN_PROGRESS)).thenReturn(3L);
        when(serviceRequestRepository.countByStatus(ServiceRequestStatus.COMPLETED)).thenReturn(3L);

        // Act
        DashboardResponse response = managerService.getDashboardStats();

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.totalRequests());
        assertEquals(4L, response.pending());
        assertEquals(3L, response.inProgress());
        assertEquals(3L, response.completed());

        verify(serviceRequestRepository).count();
        verify(serviceRequestRepository).countByStatus(ServiceRequestStatus.PENDING);
        verify(serviceRequestRepository).countByStatus(ServiceRequestStatus.IN_PROGRESS);
        verify(serviceRequestRepository).countByStatus(ServiceRequestStatus.COMPLETED);
    }

    @Test
    void getAllServices_shouldReturnPagedServices() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<ServiceOffering> servicePage = new PageImpl<>(List.of(serviceOffering), pageable, 1);

        when(serviceOfferingRepository.findServicesWithFilters("Laptop", 1L, true, pageable))
                .thenReturn(servicePage);

        // Act
        Page<ServiceOfferingResponse> responsePage = managerService.getAllServices("Laptop", 1L, true, pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        ServiceOfferingResponse response = responsePage.getContent().get(0);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.name());
        assertEquals("Repair services for laptops", response.description());
        assertEquals("IT Services", response.categoryName());
        assertEquals("{\"issue\": \"string\"}", response.formTemplate());
        assertTrue(response.isActive());

        verify(serviceOfferingRepository).findServicesWithFilters("Laptop", 1L, true, pageable);
    }

    @Test
    void createService_shouldCreateAndReturnService() {
        // Arrange
        ServiceOfferingDTO dto = new ServiceOfferingDTO("Laptop Repair", "Repair services for laptops", 1L, "{\"issue\": \"string\"}", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(serviceOfferingRepository.save(any(ServiceOffering.class))).thenReturn(serviceOffering);

        // Act
        ServiceOfferingResponse response = managerService.createService(dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.name());
        assertEquals("Repair services for laptops", response.description());
        assertEquals("IT Services", response.categoryName());
        assertEquals("{\"issue\": \"string\"}", response.formTemplate());
        assertTrue(response.isActive());

        verify(categoryRepository).findById(1L);
        verify(serviceOfferingRepository).save(any(ServiceOffering.class));
    }

    @Test
    void createService_categoryNotFound_shouldThrowNotFound() {
        // Arrange
        ServiceOfferingDTO dto = new ServiceOfferingDTO("Laptop Repair", "Repair services for laptops", 1L, "{\"issue\": \"string\"}", true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> managerService.createService(dto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Category not found", exception.getReason());

        verify(categoryRepository).findById(1L);
        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    void updateService_shouldUpdateAndReturnService() {
        // Arrange
        ServiceOfferingDTO dto = new ServiceOfferingDTO("Laptop Repair Updated", "Updated description", 1L, "{\"issue\": \"string\"}", true);
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(serviceOfferingRepository.save(any(ServiceOffering.class))).thenReturn(serviceOffering);

        // Act
        ServiceOfferingResponse response = managerService.updateService(1L, dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair Updated", response.name()); // Name is updated in the entity
        assertEquals("Updated description", response.description());
        assertEquals("IT Services", response.categoryName());
        assertEquals("{\"issue\": \"string\"}", response.formTemplate());
        assertTrue(response.isActive());

        verify(serviceOfferingRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(serviceOfferingRepository).save(any(ServiceOffering.class));
    }

    @Test
    void updateService_serviceNotFound_shouldThrowNotFound() {
        // Arrange
        ServiceOfferingDTO dto = new ServiceOfferingDTO("Laptop Repair Updated", "Updated description", 1L, "{\"issue\": \"string\"}", false);
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> managerService.updateService(1L, dto));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Service not found", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    void deleteService_shouldDeleteService() {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));

        // Act
        managerService.deleteService(1L);

        // Assert
        verify(serviceOfferingRepository).findById(1L);
        verify(serviceOfferingRepository).delete(serviceOffering);
    }

    @Test
    void deleteService_serviceNotFound_shouldThrowNotFound() {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> managerService.deleteService(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Service not found", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(serviceOfferingRepository, never()).delete(any());
    }

    @Test
    void getAllCategories_shouldReturnCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<CategoryResponse> response = managerService.getAllCategories();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        CategoryResponse categoryResponse = response.get(0);
        assertEquals(1L, categoryResponse.id());
        assertEquals("IT Services", categoryResponse.name());

        verify(categoryRepository).findAll();
    }

    @Test
    void createCategory_shouldCreateAndReturnCategory() {
        // Arrange
        CategoryDTO dto = new CategoryDTO("IT Services");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = managerService.createCategory(dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("IT Services", response.name());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getAllRequests_shouldReturnPagedRequests() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("submissionDate").descending());
        Page<ServiceRequest> requestPage = new PageImpl<>(List.of(serviceRequest), pageable, 1);

        when(serviceRequestRepository.findRequestsWithFilters(ServiceRequestStatus.PENDING, "screen", pageable))
                .thenReturn(requestPage);

        // Act
        Page<ServiceRequestResponse> responsePage = managerService.getAllRequests(ServiceRequestStatus.PENDING, "screen", pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        ServiceRequestResponse response = responsePage.getContent().get(0);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.serviceName());
        assertEquals("requester1", response.submittedBy());
        assertEquals(ServiceRequestStatus.PENDING.toString(), response.status());
        assertEquals("{\"issue\": \"screen broken\"}", response.requestData());
        assertEquals("http://example.com/attachment.pdf", response.attachmentUrl());

        verify(serviceRequestRepository).findRequestsWithFilters(ServiceRequestStatus.PENDING, "screen", pageable);
    }

    @Test
    void getRequestDetails_shouldReturnRequestDetails() {
        // Arrange
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        // Act
        ServiceRequestResponse response = managerService.getRequestDetails(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.serviceName());
        assertEquals("requester1", response.submittedBy());
        assertEquals(ServiceRequestStatus.PENDING.toString(), response.status());
        assertEquals("{\"issue\": \"screen broken\"}", response.requestData());
        assertEquals("http://example.com/attachment.pdf", response.attachmentUrl());

        verify(serviceRequestRepository).findById(1L);
    }

    @Test
    void getRequestDetails_requestNotFound_shouldThrowNotFound() {
        // Arrange
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> managerService.getRequestDetails(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Request not found", exception.getReason());

        verify(serviceRequestRepository).findById(1L);
    }

    @Test
    void updateRequestStatus_statusChanged_shouldUpdateAndSendEmail() {
        // Arrange
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> {
            ServiceRequest savedRequest = invocation.getArgument(0);
            savedRequest.setStatus(ServiceRequestStatus.IN_PROGRESS); // Simulate the status update
            return savedRequest;
        });

        // Act
        ServiceRequestResponse response = managerService.updateRequestStatus(1L, ServiceRequestStatus.IN_PROGRESS);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(ServiceRequestStatus.IN_PROGRESS.toString(), response.status()); // Status is updated in the entity

        verify(serviceRequestRepository).findById(1L);
        verify(serviceRequestRepository).save(serviceRequest);
        verify(emailService).sendRequestStatusChangeEmail(user, serviceRequest);
    }

    @Test
    void updateRequestStatus_statusNotChanged_shouldNotSendEmail() {
        // Arrange
        serviceRequest.setStatus(ServiceRequestStatus.IN_PROGRESS);
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(serviceRequest));

        // Act
        ServiceRequestResponse response = managerService.updateRequestStatus(1L, ServiceRequestStatus.IN_PROGRESS);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(ServiceRequestStatus.IN_PROGRESS.toString(), response.status());

        verify(serviceRequestRepository).findById(1L);
        verify(serviceRequestRepository, never()).save(any());
        verify(emailService, never()).sendRequestStatusChangeEmail(any(), any());
    }

    @Test
    void updateRequestStatus_requestNotFound_shouldThrowNotFound() {
        // Arrange
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> managerService.updateRequestStatus(1L, ServiceRequestStatus.IN_PROGRESS));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Request not found", exception.getReason());

        verify(serviceRequestRepository).findById(1L);
        verify(serviceRequestRepository, never()).save(any());
        verify(emailService, never()).sendRequestStatusChangeEmail(any(), any());
    }

    @Test
    void getAllRequestStatuses_shouldReturnStatuses() {
        // Act
        List<String> statuses = managerService.getAllRequestStatuses();

        // Assert
        assertNotNull(statuses);
        assertEquals(3, statuses.size()); // Assuming PENDING, IN_PROGRESS, COMPLETED
        assertTrue(statuses.contains("PENDING"));
        assertTrue(statuses.contains("IN_PROGRESS"));
        assertTrue(statuses.contains("COMPLETED"));
    }
}