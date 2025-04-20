package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    @Query("SELECT s FROM ServiceOffering s WHERE " +
            "(:name IS NULL OR s.name LIKE %:name%) AND " +
            "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
            "(:departmentId IS NULL OR s.department.id = :departmentId) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<ServiceOffering> findServicesWithFilters(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    default Page<ServiceOffering> findAvailableServices(String name, Long categoryId, Long departmentId, Pageable pageable) {
        return findServicesWithFilters(name, categoryId, departmentId, true, pageable);
    }
}
