package com.genysyxtechnologies.service_request_system.repository;

import com.genysyxtechnologies.service_request_system.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);

    List<Department> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String query, String query1);
}
