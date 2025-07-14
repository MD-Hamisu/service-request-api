package com.md.service_request_api.service;

import java.util.List;

import com.md.service_request_api.dtos.request.UpdateStatusDto;
import com.genysyxtechnologies.service_request_system.dtos.response.*;
import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.dtos.request.CategoryDTO;
import com.md.service_request_api.dtos.request.ServiceOfferingDTO;
import com.md.service_request_api.dtos.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagerService {
    DashboardResponse getDashboardStats();
    Page<ServiceOfferingResponse> getAllServices(String name, Long categoryId, Long departmentId, Boolean isActive, Pageable pageable);
    ServiceOfferingResponse createService(ServiceOfferingDTO serviceOfferingDTO);
    ServiceOfferingResponse updateService(Long id, ServiceOfferingDTO serviceOfferingDTO);
    void deleteService(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryDTO categoryDTO);
    CategoryResponse updateCategory(Long id, CategoryDTO categoryDTO);
    Page<ServiceRequestResponse> getAllRequests(ServiceRequestStatus status, String search, Pageable pageable);
    ServiceRequestResponse getRequestDetails(Long id);
    ServiceRequestResponse updateRequestStatus(Long id, UpdateStatusDto dto);
    List<String> getAllRequestStatuses();
    Page<SupervisorServiceRequestDTO> getRequestsForSupervisor(Long userDepartmentId,
                                                               Long targetDepartmentId,
                                                               ServiceRequestStatus status,
                                                               Pageable pageable);
}
