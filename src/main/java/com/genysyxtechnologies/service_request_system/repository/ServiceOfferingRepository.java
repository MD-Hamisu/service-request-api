package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    // Service Query for Requesters
    @Query(value = "SELECT * FROM services so " +
        "WHERE so.is_active = true " +
        "AND (:name IS NULL OR LOWER(so.name::text) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%'))) " +
        "AND (:categoryId IS NULL OR so.category_id = :categoryId) " +
        "ORDER BY so.created DESC",
        nativeQuery = true)
    Page<ServiceOffering> findAvailableServices(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );


    // Service Query For Managers
    @Query(value = "SELECT * FROM services so " +
        "WHERE (:name IS NULL OR LOWER(so.name::text) LIKE LOWER(CONCAT('%', COALESCE(:name,''), '%'))) " +
        "AND (:categoryId IS NULL OR so.category_id = :categoryId) " +
        "AND (:isActive IS NULL OR so.is_active = :isActive) " +
        "ORDER BY so.created DESC",
        nativeQuery = true
    )
    Page<ServiceOffering> findServicesWithFilters(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

}
