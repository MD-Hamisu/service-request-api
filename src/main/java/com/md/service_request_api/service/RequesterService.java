package com.md.service_request_api.service;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.dtos.response.CategoryResponse;
import com.md.service_request_api.dtos.response.ServiceOfferingResponse;
import com.md.service_request_api.dtos.response.ServiceRequestResponse;
import com.md.service_request_api.model.ServiceRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RequesterService {
    Page<ServiceOfferingResponse> getAvailableServices(String name, Long categoryId, Long departmentId, Pageable pageable);
    List<CategoryResponse> getCategories();
    ServiceOfferingResponse getServiceForRequestForm(Long serviceId);
    ServiceRequest submitRequest(Long serviceId, String description, Long userDepartmentId, MultipartFile attachment);
    Page<ServiceRequestResponse> getUserRequests(ServiceRequestStatus status, String search, Pageable pageable);
}
