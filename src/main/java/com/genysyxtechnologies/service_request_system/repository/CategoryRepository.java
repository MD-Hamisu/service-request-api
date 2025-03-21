package com.genysyxtechnologies.service_request_system.repository;
import com.genysyxtechnologies.service_request_system.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
