package com.genysyxtechnologies.service_request_system.model;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceOffering service;

    @ManyToOne
    @JoinColumn(name = "user_department_id", nullable = false)
    private Department userDepartment;

    @ManyToOne
    @JoinColumn(name = "target_department_id", nullable = false)
    private Department targetDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceRequestStatus status;

    @Column(name = "submission_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime submissionDate;

    @Column
    private String rejectionReason; // can be null

    @Column(length = 10000)
    private String description;

    private String attachmentUrl;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ServiceRequestStatus.PENDING;
        }
        if (submissionDate == null) {
            submissionDate = LocalDateTime.now();
        }
    }
}
