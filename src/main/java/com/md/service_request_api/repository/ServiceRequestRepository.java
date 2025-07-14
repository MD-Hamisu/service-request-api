package com.md.service_request_api.repository;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long>, JpaSpecificationExecutor<ServiceRequest> {
    long countByStatus(ServiceRequestStatus serviceRequestStatus);
}
