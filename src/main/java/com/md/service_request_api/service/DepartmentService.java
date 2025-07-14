package com.md.service_request_api.service;

import com.md.service_request_api.dtos.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(Long id);
    List<DepartmentResponse> findByQuery(String query);
    void synchronizeDepartments();
}
