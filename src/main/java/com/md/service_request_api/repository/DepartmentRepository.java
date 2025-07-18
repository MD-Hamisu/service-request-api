package com.md.service_request_api.repository;

import com.md.service_request_api.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);

    List<Department> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String query, String query1);
}
