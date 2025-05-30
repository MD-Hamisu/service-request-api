package com.genysyxtechnologies.service_request_system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.dtos.request.UpdateStatusDto;
import com.genysyxtechnologies.service_request_system.dtos.response.*;
import com.genysyxtechnologies.service_request_system.model.*;
import com.genysyxtechnologies.service_request_system.repository.DepartmentRepository;
import com.genysyxtechnologies.service_request_system.service.NotificationService;
import com.genysyxtechnologies.service_request_system.service.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.request.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.repository.CategoryRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.repository.specification.ServiceRequestSpecification;
import com.genysyxtechnologies.service_request_system.service.EmailService;
import com.genysyxtechnologies.service_request_system.service.ManagerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @Override
    public DashboardResponse getDashboardStats() {
        long totalRequests = serviceRequestRepository.count();
        long pending = serviceRequestRepository.countByStatus(ServiceRequestStatus.PENDING);
        long inProgress = serviceRequestRepository.countByStatus(ServiceRequestStatus.IN_PROGRESS);
        long underReview = serviceRequestRepository.countByStatus(ServiceRequestStatus.UNDER_REVIEW);
        long rejected = serviceRequestRepository.countByStatus(ServiceRequestStatus.REJECTED);
        long completed = serviceRequestRepository.countByStatus(ServiceRequestStatus.COMPLETED);
        return new DashboardResponse(totalRequests, pending, underReview, rejected, inProgress, completed);
    }

    @Override
    public Page<ServiceOfferingResponse> getAllServices(String name, Long categoryId, Long departmentId, Boolean isActive, Pageable pageable) {
        String nameParam = (name != null && !name.trim().isEmpty()) ? name : null;
        Page<ServiceOffering> services = serviceOfferingRepository.findServicesWithFilters(nameParam, categoryId, departmentId, isActive, pageable);
        return services.map(service -> new ServiceOfferingResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getCategory().getName(),
                service.getCategory().getId(),
                service.getDepartment().getId(),
                service.getDepartment().getName(),
                service.isActive()
        ));
    }

    @Override
    public ServiceOfferingResponse createService(ServiceOfferingDTO serviceOfferingDTO) {
        // validate the department
        var dept = departmentRepository.findById(serviceOfferingDTO.departmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

        // Check if the current user is the HOD of the target department
        User currentUser = securityUtil.getCurrentUser();
        if (!dept.getHODUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains(Role.SUPER_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the HOD of the target department can update the request status");
        }

        ServiceOffering serviceOffering = new ServiceOffering();
        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        ServiceOffering savedService = serviceOfferingRepository.save(serviceOffering);
        return new ServiceOfferingResponse(
                savedService.getId(),
                savedService.getName(),
                savedService.getDescription(),
                savedService.getCategory().getName(),
                savedService.getCategory().getId(),
                savedService.getDepartment().getId(),
                savedService.getDepartment().getName(),
                savedService.isActive()
        );
    }

    @Override
    public ServiceOfferingResponse updateService(Long id, ServiceOfferingDTO serviceOfferingDTO) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        // validate the department
        var dept = departmentRepository.findById(serviceOfferingDTO.departmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));

        // Check if the current user is the HOD of the target department
        User currentUser = securityUtil.getCurrentUser();
        if (!dept.getHODUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains(Role.SUPER_ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the HOD of the target department can update the request status");
        }

        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        ServiceOffering updatedService = serviceOfferingRepository.save(serviceOffering);
        return new ServiceOfferingResponse(
                updatedService.getId(),
                updatedService.getName(),
                updatedService.getDescription(),
                updatedService.getCategory().getName(),
                updatedService.getCategory().getId(),
                updatedService.getDepartment().getId(),
                updatedService.getDepartment().getName(),
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
    public CategoryResponse updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category.setName(categoryDTO.name());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryResponse(updatedCategory.getId(), updatedCategory.getName());
    }

    @Override
    public Page<ServiceRequestResponse> getAllRequests(ServiceRequestStatus status, String search, Pageable pageable) {
        // get current user
        var currentUser = securityUtil.getCurrentUser();
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search : null;
        var departId = currentUser.getRoles().contains(Role.SUPER_ADMIN) ? null : currentUser.getDepartment().getId();
        if(departId != null && !currentUser.getRoles().contains(Role.HOD)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission denied");
        }
        Page<ServiceRequest> requests = serviceRequestRepository.findAll(
                ServiceRequestSpecification.withFilters(status, searchTerm, departId),
                pageable
        );
        return requests.map(request -> new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getUsername(),
                request.getUserDepartment().getName(),
                request.getTargetDepartment().getName(),
            request.getSubmissionDate(),
            request.getStatus().toString(),
                request.getSubmittedData(),
                request.getAttachmentUrl(),
                request.getRejectionReason()
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
                request.getUserDepartment().getName(),
                request.getTargetDepartment().getName(),
                request.getSubmissionDate(),
                request.getStatus().toString(),
            request.getSubmittedData(),
                request.getAttachmentUrl(),
                request.getRejectionReason()
        );
    }

    @Override
    public ServiceRequestResponse updateRequestStatus(Long id, UpdateStatusDto updateStatusDto) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        // Check if the current user is the HOD of the target department
        User currentUser = securityUtil.getCurrentUser();
        if (request.getTargetDepartment().getHODUser() == null ||
                (!request.getTargetDepartment().getHODUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.SUPER_ADMIN))) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the HOD of the target department can update the request status");
        }

        // validate reason if status is rejected
        if(updateStatusDto.status().equals(ServiceRequestStatus.REJECTED) && updateStatusDto.rejectionReason() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason cannot be null");
        }

        if (!request.getStatus().equals(updateStatusDto.status())) {
            request.setStatus(updateStatusDto.status());
            request.setRejectionReason(updateStatusDto.rejectionReason());
            ServiceRequest updatedRequest = serviceRequestRepository.save(request);

            emailService.sendRequestStatusChangeEmail(updatedRequest.getUser(), updatedRequest);
            // Trigger a notification
            notificationService.createNotification(request.getId(), request.getUser().getId(), updateStatusDto.status());

            return new ServiceRequestResponse(
                    updatedRequest.getId(),
                    updatedRequest.getService().getName(),
                    updatedRequest.getUser().getUsername(),
                    updatedRequest.getUserDepartment().getName(),
                    updatedRequest.getTargetDepartment().getName(),
                    updatedRequest.getSubmissionDate(),
                    updatedRequest.getStatus().toString(),
                request.getSubmittedData(),
                    updatedRequest.getAttachmentUrl(),
                    updatedRequest.getRejectionReason()
            );
        }

        return new ServiceRequestResponse(
                request.getId(),
                request.getService().getName(),
                request.getUser().getUsername(),
                request.getUserDepartment().getName(),
                request.getTargetDepartment().getName(),
                request.getSubmissionDate(),
                request.getStatus().toString(),
            request.getSubmittedData(),
                request.getAttachmentUrl(),
                request.getRejectionReason()
        );
    }

    @Override
    public List<String> getAllRequestStatuses() {
        return Arrays.stream(ServiceRequestStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves service requests for a supervisor, with optional filtering by userDepartment and/or targetDepartment.
     *
     * @param userDepartmentId   the ID of the user department to filter by (optional, null to ignore)
     * @param targetDepartmentId the ID of the target department to filter by (optional, null to ignore)
     * @param status the status of the service request (optional, null to ignore)
     * @param pageable           pagination information
     * @return a page of SupervisorRequestDTOs
     */
    @Override
    public Page<SupervisorServiceRequestDTO> getRequestsForSupervisor(Long userDepartmentId,
                                                                      Long targetDepartmentId,
                                                                      ServiceRequestStatus status,
                                                                      Pageable pageable) {

        // Validate department IDs if provided
        if (userDepartmentId != null) {
            departmentRepository.findById(userDepartmentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User department not found"));
        }
        if (targetDepartmentId != null) {
            departmentRepository.findById(targetDepartmentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target department not found"));
        }

        // Use the Specification to filter requests
        var specification = ServiceRequestSpecification.withSupervisorFilters(userDepartmentId, targetDepartmentId, status);

        // Fetch requests using the specification
        Page<ServiceRequest> requests = serviceRequestRepository.findAll(specification, pageable);

        // Map to DTO
        return requests.map(request -> new SupervisorServiceRequestDTO(
                request.getId(),
                request.getSubmissionDate(),
                request.getStatus(),
                request.getUserDepartment().getName(),
                request.getTargetDepartment().getName()
        ));
    }

    private void mapToServiceOfferingEntity(ServiceOfferingDTO dto, ServiceOffering serviceOffering) {
        serviceOffering.setName(dto.name());
        serviceOffering.setDescription(dto.description());
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            serviceOffering.setCategory(category);
        }
        if (dto.departmentId() != null) {
            Department department = departmentRepository.findById(dto.departmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
            serviceOffering.setDepartment(department);
        }
        serviceOffering.setActive(dto.isActive());
    }

}
