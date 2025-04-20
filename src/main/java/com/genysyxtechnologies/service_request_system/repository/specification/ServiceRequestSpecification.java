package com.genysyxtechnologies.service_request_system.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import com.genysyxtechnologies.service_request_system.model.ServiceRequest;

import jakarta.persistence.criteria.Predicate;

public class ServiceRequestSpecification {

    public static Specification<ServiceRequest> withFilters(ServiceRequestStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("user").get("username")),
                    "%" + search.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ServiceRequest> withUserFilters(Long userId, ServiceRequestStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (search != null && !search.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("service").get("name")),
                    "%" + search.toLowerCase() + "%"
                ));
            }

            query.orderBy(criteriaBuilder.desc(root.get("submissionDate")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 