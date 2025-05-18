package com.genysyxtechnologies.service_request_system.service.impl;


import com.genysyxtechnologies.service_request_system.dtos.response.DepartmentResponse;
import com.genysyxtechnologies.service_request_system.model.Department;
import com.genysyxtechnologies.service_request_system.repository.DepartmentRepository;
import com.genysyxtechnologies.service_request_system.service.DepartmentService;
import com.genysyxtechnologies.service_request_system.service.util.sync.APIKeyService;
import com.genysyxtechnologies.service_request_system.service.util.sync.NetworkRequest;
import com.genysyxtechnologies.service_request_system.service.util.sync.URLConstants;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final NetworkRequest<String, JSONArray> networkRequest;
    private final APIKeyService apiKeyService;
    private final Function<String, JSONArray> conv = (s) -> {
        if(s.isBlank())
            return new JSONArray();
        return new JSONArray(s);
    };

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName(), dept.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName(), dept.getCode()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
    }

    @Override
    public List<DepartmentResponse> findByQuery(String query) {
        return departmentRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query)
                .stream()
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName(), dept.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public void synchronizeDepartments() {
        // Make network call
        var resp = networkRequest.makeRequest(
                URLConstants.GET_DEPARTMENTS,
                new HashMap<>(),
                HttpResponse.BodyHandlers.ofString(),
                conv,
                () -> {},
                apiKeyService
        );

        // Process data
        for (int i = 0; i < resp.length(); i++) {
            var jsonObject = resp.getJSONObject(i);
            var department = new Department();
            department.setId(jsonObject.getLong("id"));
            department.setName(jsonObject.getString("name"));
            department.setCode(jsonObject.getString("code"));
            departmentRepository.save(department);
        }
    }
}
