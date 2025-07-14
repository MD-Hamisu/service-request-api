package com.md.service_request_api.repository.specification;

import com.md.service_request_api.constant.ServiceRequestStatus;
import com.md.service_request_api.model.ServiceRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceRequestSpecification {

    public static Specification<ServiceRequest> withFilters(ServiceRequestStatus status, String searchTerm, Long departmentId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if(departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("service").get("department").get("id"), departmentId));
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

                // Add sorting by submissionDate (descending)
                assert query != null;
                query.orderBy(criteriaBuilder.desc(root.get("submissionDate")));

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

    /**
     * Creates a Specification for filtering service requests by user department and/or target department.
     *
     * @param userDepartmentId   the ID of the user department to filter by (optional, null to ignore)
     * @param targetDepartmentId the ID of the target department to filter by (optional, null to ignore)
     * @return a Specification for ServiceRequest
     */
    public static Specification<ServiceRequest> withSupervisorFilters(Long userDepartmentId,
                                                                      Long targetDepartmentId,
                                                                      ServiceRequestStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userDepartmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userDepartment").get("id"), userDepartmentId));
            }

            if (targetDepartmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("targetDepartment").get("id"), targetDepartmentId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

