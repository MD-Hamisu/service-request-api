package com.md.service_request_api.specification;

import com.md.service_request_api.constant.Role;
import com.md.service_request_api.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasRole(Role role) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.isMember(role, root.get("roles"));
        };
    }

    public static Specification<User> searchByUsernameOrEmail(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern)
            );
        };
    }
}
