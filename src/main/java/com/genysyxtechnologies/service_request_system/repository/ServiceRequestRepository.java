package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findAllByUser_Id(Long userId);
    List<ServiceRequest> findAllByUserIdAndStatus(Long user_id, ServiceRequestStatus status);
    List<ServiceRequest> findAllByStatus(ServiceRequestStatus status);

    @Query("SELECT sr FROM ServiceRequest sr " +
            "WHERE sr.user.id = :userId " +
            "AND (:status IS NULL OR sr.status = :status) " +
            "AND (:search IS NULL OR " +
            "LOWER(sr.service.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(sr.submittedData) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY sr.submissionDate DESC")
    Page<ServiceRequest> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") ServiceRequestStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    long countByStatus(ServiceRequestStatus serviceRequestStatus);

    @Query("SELECT sr FROM ServiceRequest sr " +
            "WHERE (:status IS NULL OR sr.status = :status) " +
            "AND (:search IS NULL OR " +
            "LOWER(sr.service.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(sr.submittedData) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY sr.submissionDate DESC")
    Page<ServiceRequest> findRequestsWithFilters(
            @Param("status") ServiceRequestStatus status,
            @Param("search") String search,
            Pageable pageable
    );
}
