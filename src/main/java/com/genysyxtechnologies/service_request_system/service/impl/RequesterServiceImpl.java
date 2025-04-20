package com.genysyxtechnologies.service_request_system.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.genysyxtechnologies.service_request_system.model.Department;
import com.genysyxtechnologies.service_request_system.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
import com.genysyxtechnologies.service_request_system.repository.specification.ServiceRequestSpecification;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import com.genysyxtechnologies.service_request_system.service.RequesterService;
import com.genysyxtechnologies.service_request_system.service.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequesterServiceImpl implements RequesterService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final SecurityUtil securityUtil;
    private final ObjectMapper jacksonObjectMapper;
    private final CategoryRepository categoryRepository;
    private final EmailService emailService;

    @Override
    public Page<ServiceOfferingResponse> getAvailableServices(String name, Long categoryId, Long departmentId, Pageable pageable) {
        String nameParam = (name != null && !name.trim().isEmpty()) ? name : null;
        Page<ServiceOffering> services = serviceOfferingRepository.findAvailableServices(nameParam, categoryId, departmentId, pageable);
        return services.map(service -> new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getCategory().getId(),
                service.getDepartment().getId(),
                service.getDepartment().getName(),
                service.getFieldSchema(),
                service.isActive()
        ));
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceOfferingResponse getServiceForRequestForm(Long serviceId) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        if (!service.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service is not active");
        }

        return new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getCategory().getId(),
                service.getDepartment().getId(),
                service.getDepartment().getName(),
                service.getFieldSchema(),
                service.isActive()
        );
    }

    @Override
    public ServiceRequest submitRequest(Long serviceId, String requestData, Long userDepartmentId, Long targetDepartmentId, MultipartFile attachment) {
        ServiceOffering service = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        if (!service.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service is not active");
        }

        User user = securityUtil.getCurrentUser();

        Department userDepartment = departmentRepository.findById(userDepartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User department not found"));

        Department targetDepartment = departmentRepository.findById(targetDepartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target department not found"));

        if (!targetDepartment.getId().equals(service.getDepartment().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target department does not match the service's department");
        }

        validateRequestData(service.getFieldSchema(), requestData);

        String attachmentUrl = null;
        if (attachment != null && !attachment.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + attachment.getOriginalFilename();
            File file = new File("uploads/" + fileName);
            try {
                attachment.transferTo(file);
                attachmentUrl = "/uploads/" + fileName;
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload attachment");
            }
        }

        ServiceRequest request = new ServiceRequest();
        request.setUser(user);
        request.setService(service);
        request.setUserDepartment(userDepartment);
        request.setTargetDepartment(targetDepartment);
        request.setStatus(ServiceRequestStatus.PENDING);
        request.setSubmissionDate(LocalDateTime.now());
        request.setSubmittedData(requestData);
        request.setAttachmentUrl(attachmentUrl);
        serviceRequestRepository.save(request);

        emailService.sendRequestSubmissionEmail(user, request);

        return request;
    }

    private void validateRequestData(String formTemplate, String requestData) {
        try {
            JsonNode templateNode = jacksonObjectMapper.readTree(formTemplate);
            JsonNode dataNode = jacksonObjectMapper.readTree(requestData);

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
        Page<ServiceRequest> requests = serviceRequestRepository.findAll(
                ServiceRequestSpecification.withUserFilters(user.getId(), status, searchTerm),
                pageable
        );

        return requests.map(request -> new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getFullName(),
                request.getUserDepartment().getName(),
                request.getTargetDepartment().getName(),
                request.getSubmissionDate().toString(),
                request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl()
        ));
    }
}
