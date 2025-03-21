package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    Page<ServiceOffering> findAllByActiveIsTrue(Pageable pageable);

    Page<ServiceOffering> findByNameContainingIgnoreCaseAAndActiveIsTrue(String name, Pageable pageable);
}
