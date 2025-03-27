package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    @Query("SELECT so FROM ServiceOffering so " +
            "WHERE so.isActive = true " +
            "AND (:name IS NULL OR LOWER(so.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:categoryId IS NULL OR so.category.id = :categoryId)")
    Page<ServiceOffering> findAvailableServices(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("SELECT so FROM ServiceOffering so " +
            "WHERE (:name IS NULL OR LOWER(so.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:categoryId IS NULL OR so.category.id = :categoryId) " +
            "AND (:isActive IS NULL OR so.isActive = :isActive)"+
            "ORDER BY so.created DESC")
    Page<ServiceOffering> findServicesWithFilters(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

}
