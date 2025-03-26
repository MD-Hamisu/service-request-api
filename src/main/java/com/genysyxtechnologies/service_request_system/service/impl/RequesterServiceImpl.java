package com.genysyxtechnologies.service_request_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceOfferingResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceRequestResponse;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.CategoryRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.service.RequesterService;
import com.genysyxtechnologies.service_request_system.service.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequesterServiceImpl implements RequesterService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final SecurityUtil securityUtil;
    private final ObjectMapper jacksonObjectMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ServiceOfferingResponse> getAvailableServices(String name, Long categoryId, Pageable pageable) {
        String nameParam = (name != null && !name.trim().isEmpty()) ? name : null;
        Page<ServiceOffering> services = serviceOfferingRepository.findAvailableServices(nameParam, categoryId, pageable);
        return services.map(service -> new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getFieldSchema()
        ));
    }

    @Override
    public List<CategoryResponse> getCategories() {
        // Fetch all categories, map to DTO and return
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .collect(Collectors.toList());
    }

    // Fetches a specific ServiceOffering by serviceId to render the new request form, including its fieldSchema
    @Override
    public ServiceOfferingResponse getServiceForRequestForm(Long serviceId) {
        // Fetch the service by ID
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        // Ensure the service is active
        if (!service.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service is not active");
        }

        // Map to DTO and return
        return new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getFieldSchema()
        );
    }

    @Override
    public String submitRequest(Long serviceId, String requestData, MultipartFile attachment) {
        // Validate service
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        if (!service.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service is not active");
        }

        // Get authenticated user
        User user = securityUtil.getCurrentUser();

        // Validate requestData against formTemplate
        validateRequestData(service.getFieldSchema(), requestData);

        // Handle attachment (if any)
        String attachmentUrl = null;
        if (attachment != null && !attachment.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + attachment.getOriginalFilename();
            File file = new File("uploads/" + fileName); // Ensure "uploads" directory exists
            try {
                attachment.transferTo(file);
                attachmentUrl = "/uploads/" + fileName; // Adjust for production (e.g., S3)
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload attachment");
            }
        }

        // Create ServiceRequest
        ServiceRequest request = new ServiceRequest();
        request.setUser(user);
        request.setService(service);
        request.setStatus(ServiceRequestStatus.PENDING);
        request.setSubmissionDate(LocalDateTime.now());
        request.setSubmittedData(requestData);
        request.setAttachmentUrl(attachmentUrl);
        serviceRequestRepository.save(request);

        return "Request submitted successfully";
    }

    // Helper method to validate Request submitted Json data over the Service DataSchema
    private void validateRequestData(String formTemplate, String requestData) {
        try {
            // Parse formTemplate and requestData as JSON
            JsonNode templateNode = jacksonObjectMapper.readTree(formTemplate);
            JsonNode dataNode = jacksonObjectMapper.readTree(requestData);

            // Check for extra fields in requestData that are not in formTemplate
            for (Iterator<String> it = dataNode.fieldNames(); it.hasNext(); ) {
                String fieldName = it.next();
                if (!templateNode.has(fieldName)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Unexpected field in request data: " + fieldName);
                }
            }
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format: " + e.getMessage());
        }
    }

    @Override
    public Page<ServiceRequestResponse> getUserRequests(ServiceRequestStatus status, String search, Pageable pageable) {
        User user = securityUtil.getCurrentUser();
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search : null;
        Page<ServiceRequest> requests = serviceRequestRepository.findByUserIdWithFilters(
                user.getId(),
                status,
                searchTerm,
                pageable
        );

        // convert to a response Dto and return
        return requests.map(request -> new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getSubmissionDate().toString(),
                request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl()
        ));
    }
}
