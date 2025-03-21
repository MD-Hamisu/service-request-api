package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.model.Category;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;

import java.util.List;

public interface ManagerService {
    ServiceOffering createService(ServiceOfferingDTO serviceOfferingDTO);
    ServiceOffering updateService(Long id, ServiceOfferingDTO serviceOfferingDTO);
    void deleteService(Long id);
    List<ServiceOffering> getAllServices();
    Category createCategory(CategoryDTO categoryDTO);
    List<ServiceRequest> getAllRequests(ServiceRequestStatus status);
    ServiceRequest updateRequestStatus(Long id, ServiceRequestStatus status);
    List<String> getAllRequestStatuses();
}
