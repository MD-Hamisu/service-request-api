package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.response.CategoryResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceOfferingResponse;
import com.genysyxtechnologies.service_request_system.dtos.response.ServiceRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RequesterService {
    Page<ServiceOfferingResponse> getAvailableServices(String name, Long categoryId, Pageable pageable);
    List<CategoryResponse> getCategories();
    ServiceOfferingResponse getServiceForRequestForm(Long serviceId);
    String submitRequest(Long serviceId, String requestData, MultipartFile attachment);
    Page<ServiceRequestResponse> getUserRequests(ServiceRequestStatus status, String search, Pageable pageable);
}
