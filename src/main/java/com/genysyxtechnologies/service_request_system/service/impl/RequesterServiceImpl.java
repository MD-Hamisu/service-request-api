package com.genysyxtechnologies.service_request_system.service.impl;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.dtos.ServiceRequestDTO;
import com.genysyxtechnologies.service_request_system.model.ServiceOffering;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import com.genysyxtechnologies.service_request_system.model.User;
import com.genysyxtechnologies.service_request_system.repository.ServiceOfferingRepository;
import com.genysyxtechnologies.service_request_system.repository.ServiceRequestRepository;
import com.genysyxtechnologies.service_request_system.repository.UserRepository;
import com.genysyxtechnologies.service_request_system.service.RequesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequesterServiceImpl implements RequesterService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ServiceOffering> getAvailableServices(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return serviceOfferingRepository.findByNameContainingIgnoreCaseAAndActiveIsTrue(name,pageable);
        }
        return serviceOfferingRepository.findAllByActiveIsTrue(pageable);
    }

    @Override
    public ServiceRequest submitRequest(ServiceRequestDTO serviceRequestDTO) {
        ServiceRequest request = mapToRequestEntity(serviceRequestDTO);
        return serviceRequestRepository.save(request);
    }

    @Override
    public List<ServiceRequest> getUserRequests(Long userId, ServiceRequestStatus status) {
        return serviceRequestRepository.findAllByUserIdAndStatus(userId, status);
    }

    // Mapping Methods
    private ServiceRequest mapToRequestEntity(ServiceRequestDTO dto) {
        ServiceRequest request = new ServiceRequest();
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        request.setUser(user);
        ServiceOffering service = serviceOfferingRepository.findById(dto.getServiceOfferingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        request.setService(service);
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : request.getStatus());
        request.setSubmittedData(dto.getFieldsData());
        return request;
    }
}
