package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.ServiceRequestDTO;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequesterService {
    Page<ServiceOffering> getAvailableServices(String name, Pageable pageable);
    ServiceRequest submitRequest(ServiceRequestDTO serviceRequestDTO);
    List<ServiceRequest> getUserRequests(Long userId, ServiceRequestStatus status);
}
