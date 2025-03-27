package com.genysyxtechnologies.service_request_system.service.impl;

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
import com.genysyxtechnologies.service_request_system.repository.CategoryRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import com.genysyxtechnologies.service_request_system.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final EmailService emailService;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalRequests = serviceRequestRepository.count();
        long pending = serviceRequestRepository.countByStatus(ServiceRequestStatus.PENDING);
        long inProgress = serviceRequestRepository.countByStatus(ServiceRequestStatus.IN_PROGRESS);
        long completed = serviceRequestRepository.countByStatus(ServiceRequestStatus.COMPLETED);
        return new DashboardResponse(totalRequests, pending, inProgress, completed);
    }

    @Override
    public Page<ServiceOfferingResponse> getAllServices(String name, Long categoryId, Boolean isActive, Pageable pageable) {
        String nameParam = (name != null && !name.trim().isEmpty()) ? name : null;
        Page<ServiceOffering> services = serviceOfferingRepository.findServicesWithFilters(nameParam, categoryId, isActive, pageable);
        return services.map(service -> new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getFieldSchema(),
                service.isActive()
        ));
    }

    @Override
    public ServiceOfferingResponse createService(ServiceOfferingDTO serviceOfferingDTO) {
        ServiceOffering serviceOffering = new ServiceOffering();
        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        ServiceOffering savedService = serviceOfferingRepository.save(serviceOffering);
        return new ServiceOfferingResponse(
                savedService.getId(),
                savedService.getName(),
                savedService.getDescription(),
                savedService.getCategory().getName(),
                savedService.getFieldSchema(),
                savedService.isActive()
        );
    }

    @Override
    public ServiceOfferingResponse updateService(Long id, ServiceOfferingDTO serviceOfferingDTO) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        ServiceOffering updatedService = serviceOfferingRepository.save(serviceOffering);
        return new ServiceOfferingResponse(
                updatedService.getId(),
                updatedService.getName(),
                updatedService.getDescription(),
                updatedService.getCategory().getName(),
                updatedService.getFieldSchema(),
                updatedService.isActive()
        );
    }

    @Override
    public void deleteService(Long id) {
        serviceOfferingRepository.findById(id)
                .ifPresentOrElse(
                        serviceOfferingRepository::delete,
                        () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"); }
                );
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.name());
        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory.getId(), savedCategory.getName());
    }

    @Override
    public Page<ServiceRequestResponse> getAllRequests(ServiceRequestStatus status, String search, Pageable pageable) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search : null;
        Page<ServiceRequest> requests = serviceRequestRepository.findRequestsWithFilters(status, searchTerm, pageable);
        return requests.map(request -> new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getUsername(),
                request.getSubmissionDate().toString(),
                request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl()
        ));
    }

    @Override
    public ServiceRequestResponse getRequestDetails(Long id) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        return new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getUsername(),
                request.getSubmissionDate().toString(),
                request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl()
        );
    }

    @Override
    public ServiceRequestResponse updateRequestStatus(Long id, ServiceRequestStatus status) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        // Check if the status is actually changing
        if (!request.getStatus().equals(status)) {
            request.setStatus(status);
            ServiceRequest updatedRequest = serviceRequestRepository.save(request);

            // Send email notification to the Requester
            emailService.sendRequestStatusChangeEmail(updatedRequest.getUser(), updatedRequest);

            return new ServiceRequestResponse(
                    updatedRequest.getId(),
                    updatedRequest.getService().getName(),
                    updatedRequest.getUser().getUsername(),
                    updatedRequest.getSubmissionDate().toString(),
                    updatedRequest.getStatus().toString(),
                    updatedRequest.getSubmittedData(),
                    updatedRequest.getAttachmentUrl()
            );
        }

        // If status didn't change, return the current request without sending an email
        return new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getUsername(),
                request.getSubmissionDate().toString(),
                request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl()
        );
    }

    @Override
    public List<String> getAllRequestStatuses() {
        return Arrays.stream(ServiceRequestStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // Mapping Method
    private void mapToServiceOfferingEntity(ServiceOfferingDTO dto, ServiceOffering serviceOffering) {
        serviceOffering.setName(dto.getName());
        serviceOffering.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            serviceOffering.setCategory(category);
        }
        serviceOffering.setFieldSchema(dto.getFields());
        serviceOffering.setActive(dto.isActive());
    }
}
