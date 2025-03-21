package com.genysyxtechnologies.service_request_system.service.impl;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.CategoryDTO;
import com.genysyxtechnologies.service_request_system.dtos.ServiceOfferingDTO;
import com.genysyxtechnologies.service_request_system.model.Category;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.repository.CategoryRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Override
    public ServiceOffering createService(ServiceOfferingDTO serviceOfferingDTO) {
        ServiceOffering serviceOffering = new ServiceOffering();
        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        return serviceOfferingRepository.save(serviceOffering);
    }

    @Override
    public ServiceOffering updateService(Long id, ServiceOfferingDTO serviceOfferingDTO) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));
        mapToServiceOfferingEntity(serviceOfferingDTO, serviceOffering);
        return serviceOfferingRepository.save(serviceOffering);
    }

    @Override
    public void deleteService(Long id) {
        serviceOfferingRepository.findById(id)
                .ifPresentOrElse(
                        serviceOfferingRepository::delete,
                        () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"); }
                );
    }

    @Override
    public List<ServiceOffering> getAllServices() {
        return serviceOfferingRepository.findAll();
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    @Override
    public List<ServiceRequest> getAllRequests(ServiceRequestStatus status) {
        if(status != null){
            return serviceRequestRepository.findAllByStatus(status);
        }
        return serviceRequestRepository.findAll();
    }

    @Override
    public ServiceRequest updateRequestStatus(Long id, ServiceRequestStatus status) {
        ServiceRequest request = serviceRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        return serviceRequestRepository.save(request);
    }

    @Override
    public List<String> getAllRequestStatuses() {
        return Arrays.stream(ServiceRequestStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // Mapping Method
    private void mapToServiceOfferingEntity(ServiceOfferingDTO dto, ServiceOffering serviceOffering) {
        serviceOffering.setName(dto.getName());
        serviceOffering.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            serviceOffering.setCategory(category);
        }
        serviceOffering.setFieldSchema(dto.getFields());
        serviceOffering.setActive(dto.isActive());
    }
}
