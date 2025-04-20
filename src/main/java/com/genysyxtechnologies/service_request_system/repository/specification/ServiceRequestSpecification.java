package com.genysyxtechnologies.service_request_system.repository.specification;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceRequestSpecification {

    public static Specification<ServiceRequest> withFilters(ServiceRequestStatus status, String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                Predicate serviceNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("service").get("name")),
                        "%" + searchTerm.toLowerCase() + "%"
                );

                Predicate usernamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("username")),
                        "%" + searchTerm.toLowerCase() + "%"
                );

                predicates.add(criteriaBuilder.or(serviceNamePredicate, usernamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ServiceRequest> withUserFilters(Long userId, ServiceRequestStatus status, String searchTerm) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likeSearch = "%" + searchTerm.toLowerCase() + "%";
                Predicate serviceNamePredicate = cb.like(cb.lower(root.get("service").get("name")), likeSearch);
                Predicate submittedDataPredicate = cb.like(cb.lower(root.get("submittedData")), likeSearch);
                predicates.add(cb.or(serviceNamePredicate, submittedDataPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

