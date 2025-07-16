/*
package com.md.service_request_api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.service_request_api.constant.Role;
import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.dtos.response.CategoryResponse;
import com.md.service_request_api.dtos.response.ServiceOfferingResponse;
import com.md.service_request_api.dtos.response.ServiceRequestResponse;
import com.md.service_request_api.model.Category;
import com.md.service_request_api.model.ServiceOffering;
import com.md.service_request_api.model.ServiceRequest;
import com.md.service_request_api.model.User;
import com.md.service_request_api.repository.CategoryRepository;
import com.md.service_request_api.repository.ServiceOfferingRepository;
import com.md.service_request_api.repository.ServiceRequestRepository;
import com.md.service_request_api.service.EmailService;
import com.md.service_request_api.service.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequesterServiceImplTest {

    @Mock
    private ServiceOfferingRepository serviceOfferingRepository;

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ObjectMapper jacksonObjectMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RequesterServiceImpl requesterService;

    private Category category;
    private ServiceOffering serviceOffering;
    private ServiceRequest serviceRequest;
    private User user;
    private MultipartFile attachment;

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
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoles(Set.of(Role.REQUESTER));

        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        serviceRequest.setService(serviceOffering);
        serviceRequest.setUser(user);
        serviceRequest.setSubmissionDate(LocalDateTime.now());
        serviceRequest.setStatus(ServiceRequestStatus.PENDING);
        serviceRequest.setSubmittedData("{\"issue\": \"screen broken\"}");
        serviceRequest.setAttachmentUrl("http://example.com/attachment.pdf");

        attachment = new MockMultipartFile(
                "attachment",
                "test.pdf",
                "application/pdf",
                "Test content".getBytes()
        );
    }

    @Test
    void getAvailableServices_shouldReturnPagedServices() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<ServiceOffering> servicePage = new PageImpl<>(List.of(serviceOffering), pageable, 1);

        when(serviceOfferingRepository.findAvailableServices("Laptop", 1L, pageable))
                .thenReturn(servicePage);

        // Act
        Page<ServiceOfferingResponse> responsePage = requesterService.getAvailableServices("Laptop", 1L, pageable);

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

        verify(serviceOfferingRepository).findAvailableServices("Laptop", 1L, pageable);
    }

    @Test
    void getCategories_shouldReturnCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<CategoryResponse> response = requesterService.getCategories();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        CategoryResponse categoryResponse = response.get(0);
        assertEquals(1L, categoryResponse.id());
        assertEquals("IT Services", categoryResponse.name());

        verify(categoryRepository).findAll();
    }

    @Test
    void getServiceForRequestForm_shouldReturnService() {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));

        // Act
        ServiceOfferingResponse response = requesterService.getServiceForRequestForm(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.name());
        assertEquals("Repair services for laptops", response.description());
        assertEquals("IT Services", response.categoryName());
        assertEquals("{\"issue\": \"string\"}", response.formTemplate());
        assertTrue(response.isActive());

        verify(serviceOfferingRepository).findById(1L);
    }

    @Test
    void getServiceForRequestForm_serviceNotFound_shouldThrowNotFound() {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.getServiceForRequestForm(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Service not found", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
    }

    @Test
    void getServiceForRequestForm_serviceNotActive_shouldThrowBadRequest() {
        // Arrange
        serviceOffering.setActive(false);
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.getServiceForRequestForm(1L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Service is not active", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
    }

    @Test
    void submitRequest_withAttachment_shouldSubmitSuccessfully() throws IOException {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        JsonNode templateNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        when(jacksonObjectMapper.readTree("{\"issue\": \"string\"}")).thenReturn(templateNode);
        when(jacksonObjectMapper.readTree("{\"issue\": \"screen broken\"}")).thenReturn(dataNode);
        when(templateNode.has("issue")).thenReturn(true);
        when(dataNode.fieldNames()).thenReturn(new Iterator<String>() {
            private final String[] fields = {"issue"};
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < fields.length;
            }

            @Override
            public String next() {
                return fields[index++];
            }
        });

        // Mock the MultipartFile to simulate a successful file transfer
        MultipartFile mockAttachment = mock(MultipartFile.class);
        when(mockAttachment.isEmpty()).thenReturn(false);
        when(mockAttachment.getOriginalFilename()).thenReturn("test.pdf");
        doNothing().when(mockAttachment).transferTo(any(File.class)); // Simulate successful file transfer

        // Act
        String result = requesterService.submitRequest(1L, "{\"issue\": \"screen broken\"}", mockAttachment);

        // Assert
        assertEquals("Request submitted successfully", result);

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil).getCurrentUser();
        verify(jacksonObjectMapper, times(2)).readTree(anyString());
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
        verify(emailService).sendRequestSubmissionEmail(any(User.class), any(ServiceRequest.class));
    }

    @Test
    void submitRequest_withoutAttachment_shouldSubmitSuccessfully() throws IOException {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        JsonNode templateNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        when(jacksonObjectMapper.readTree("{\"issue\": \"string\"}")).thenReturn(templateNode);
        when(jacksonObjectMapper.readTree("{\"issue\": \"screen broken\"}")).thenReturn(dataNode);
        when(templateNode.has("issue")).thenReturn(true);
        when(dataNode.fieldNames()).thenReturn(new Iterator<String>() {
            private final String[] fields = {"issue"};
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < fields.length;
            }

            @Override
            public String next() {
                return fields[index++];
            }
        });

        // Act
        String result = requesterService.submitRequest(1L, "{\"issue\": \"screen broken\"}", null);

        // Assert
        assertEquals("Request submitted successfully", result);

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil).getCurrentUser();
        verify(jacksonObjectMapper, times(2)).readTree(anyString());
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
        verify(emailService).sendRequestSubmissionEmail(any(User.class), any(ServiceRequest.class));
    }

    @Test
    void submitRequest_serviceNotFound_shouldThrowNotFound() {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.submitRequest(1L, "{\"issue\": \"screen broken\"}", null));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Service not found", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil, never()).getCurrentUser();
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void submitRequest_serviceNotActive_shouldThrowBadRequest() {
        // Arrange
        serviceOffering.setActive(false);
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.submitRequest(1L, "{\"issue\": \"screen broken\"}", null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Service is not active", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil, never()).getCurrentUser();
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void submitRequest_invalidRequestData_shouldThrowBadRequest() throws JsonProcessingException {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        JsonNode templateNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        when(jacksonObjectMapper.readTree("{\"issue\": \"string\"}")).thenReturn(templateNode);
        when(jacksonObjectMapper.readTree("{\"invalidField\": \"value\"}")).thenReturn(dataNode);
        when(templateNode.has("invalidField")).thenReturn(false);
        when(dataNode.fieldNames()).thenReturn(new Iterator<String>() {
            private final String[] fields = {"invalidField"};
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < fields.length;
            }

            @Override
            public String next() {
                return fields[index++];
            }
        });

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.submitRequest(1L, "{\"invalidField\": \"value\"}", null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Unexpected field in request data: invalidField", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil).getCurrentUser();
        verify(jacksonObjectMapper, times(2)).readTree(anyString());
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void submitRequest_invalidJson_shouldThrowBadRequest() throws JsonProcessingException {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(jacksonObjectMapper.readTree("{\"issue\": \"string\"}")).thenReturn(mock(JsonNode.class));
        when(jacksonObjectMapper.readTree("invalid-json")).thenThrow(new JsonProcessingException("Invalid JSON") {});

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.submitRequest(1L, "invalid-json", null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().startsWith("Invalid JSON format"));

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil).getCurrentUser();
        verify(jacksonObjectMapper, times(2)).readTree(anyString());
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void submitRequest_attachmentUploadFails_shouldThrowInternalServerError() throws IOException {
        // Arrange
        when(serviceOfferingRepository.findById(1L)).thenReturn(Optional.of(serviceOffering));
        when(securityUtil.getCurrentUser()).thenReturn(user);
        JsonNode templateNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        when(jacksonObjectMapper.readTree("{\"issue\": \"string\"}")).thenReturn(templateNode);
        when(jacksonObjectMapper.readTree("{\"issue\": \"screen broken\"}")).thenReturn(dataNode);
        when(templateNode.has("issue")).thenReturn(true);
        when(dataNode.fieldNames()).thenReturn(new Iterator<String>() {
            private final String[] fields = {"issue"};
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < fields.length;
            }

            @Override
            public String next() {
                return fields[index++];
            }
        });

        // Simulate IOException during file transfer
        MultipartFile failingAttachment = mock(MultipartFile.class);
        when(failingAttachment.isEmpty()).thenReturn(false);
        doThrow(new IOException("File transfer failed")).when(failingAttachment).transferTo(any(File.class));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> requesterService.submitRequest(1L, "{\"issue\": \"screen broken\"}", failingAttachment));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to upload attachment", exception.getReason());

        verify(serviceOfferingRepository).findById(1L);
        verify(securityUtil).getCurrentUser();
        verify(jacksonObjectMapper, times(2)).readTree(anyString());
        verify(serviceRequestRepository, never()).save(any());
    }

    @Test
    void getUserRequests_shouldReturnPagedRequests() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("submissionDate").descending());
        Page<ServiceRequest> requestPage = new PageImpl<>(List.of(serviceRequest), pageable, 1);

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(serviceRequestRepository.findByUserIdWithFilters(user.getId(), ServiceRequestStatus.PENDING, "screen", pageable))
                .thenReturn(requestPage);

        // Act
        Page<ServiceRequestResponse> responsePage = requesterService.getUserRequests(ServiceRequestStatus.PENDING, "screen", pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        ServiceRequestResponse response = responsePage.getContent().get(0);
        assertEquals(1L, response.id());
        assertEquals("Laptop Repair", response.serviceName());
        assertEquals("John Doe", response.submittedBy());
        assertEquals(ServiceRequestStatus.PENDING.toString(), response.status());
        assertEquals("{\"issue\": \"screen broken\"}", response.requestData());
        assertEquals("http://example.com/attachment.pdf", response.attachmentUrl());

        verify(securityUtil).getCurrentUser();
        verify(serviceRequestRepository).findByUserIdWithFilters(user.getId(), ServiceRequestStatus.PENDING, "screen", pageable);
    }
}
*/
