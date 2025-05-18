package com.genysyxtechnologies.service_request_system.repository;


import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String identifier);

    long countByRolesContaining(Role role);

    @Query(value = "SELECT u.* FROM users u " +
        "JOIN user_roles ur ON u.id = ur.user_id " +
        "WHERE ur.role = CAST(:role AS VARCHAR) " + // Cast the role parameter to VARCHAR
        "AND (:search IS NULL OR " +
        "LOWER(u.username::text) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(u.email::text) LIKE LOWER(CONCAT('%', :search, '%')))", nativeQuery = true)
    Page<User> findByRoleWithFilters(
        @Param("role") Role role,
        @Param("search") String search,
        Pageable pageable
    );
}
