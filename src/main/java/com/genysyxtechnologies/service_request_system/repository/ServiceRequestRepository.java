package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    @Query("SELECT sr FROM ServiceRequest sr " +
            "WHERE sr.user.id = :userId " +
            "AND (:status IS NULL OR sr.status = :status) " +
            "AND (:search IS NULL OR sr.service.name LIKE CONCAT('%', :search, '%'))" +
            "ORDER BY sr.submissionDate DESC")
    Page<ServiceRequest> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") ServiceRequestStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    long countByStatus(ServiceRequestStatus serviceRequestStatus);

    @Query("SELECT sr FROM ServiceRequest sr WHERE (:status IS NULL OR sr.status = :status) AND " +
            "(:search IS NULL OR sr.user.username LIKE CONCAT('%', :search, '%'))")
    Page<ServiceRequest> findRequestsWithFilters(
            @Param("status") ServiceRequestStatus status,
            @Param("search") String search,
            Pageable pageable
    );
}
