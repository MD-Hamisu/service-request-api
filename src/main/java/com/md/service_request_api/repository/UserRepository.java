package com.md.service_request_api.repository;


import com.md.service_request_api.constant.Role;
import com.md.service_request_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String identifier);

    long countByRolesContaining(Role role);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role " +
            "AND (:search IS NULL OR :search = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByRoleWithFilters(
            @Param("role") Role role,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles " +
        "AND (:search IS NULL OR :search = '' OR " +
        "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByRolesWithFilters(
        @Param("roles") Set<Role> roles,
        @Param("search") String search,
        Pageable pageable
    );


    Optional<User> findByRolesContainingAndDepartmentId(Role role, Long departmentId);
}
