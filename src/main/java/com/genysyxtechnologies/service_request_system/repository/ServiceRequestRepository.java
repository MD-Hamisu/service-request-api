package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findAllByUserId(Long userId);
    List<ServiceRequest> findAllByUserIdAndStatus(Long user_id, ServiceRequestStatus status);
    List<ServiceRequest> findAllByStatus(ServiceRequestStatus status);
}
