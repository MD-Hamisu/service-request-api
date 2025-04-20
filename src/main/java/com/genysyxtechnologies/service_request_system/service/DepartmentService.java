package com.genysyxtechnologies.service_request_system.service;

import com.genysyxtechnologies.service_request_system.dtos.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(Long id);
    List<DepartmentResponse> findByQuery(String query);
    void synchronizeDepartments();
}
