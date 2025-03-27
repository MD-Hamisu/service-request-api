package com.genysyxtechnologies.service_request_system.repository;


import com.genysyxtechnologies.service_request_system.constant.Role;
import com.genysyxtechnologies.service_request_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String identifier);

    long countByRolesContaining(Role role);

    @Query("SELECT u FROM User u " +
            "WHERE :role IN elements(u.roles) " +
            "AND (:search IS NULL OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByRoleWithFilters(
            @Param("role") Role role,
            @Param("search") String search,
            Pageable pageable
    );
}
