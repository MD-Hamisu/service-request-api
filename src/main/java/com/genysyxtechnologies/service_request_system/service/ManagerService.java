package com.genysyxtechnologies.service_request_system.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.request.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.request.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.DashboardResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceOfferingResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceRequestResponse;

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
    ServiceRequestResponse updateRequestStatus(Long id, ServiceRequestStatus status);
    List<String> getAllRequestStatuses();
}
