package com.genysyxtechnologies.service_request_system.repository;


import com.genysyxtechnologies.service_request_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
