package com.genysyxtechnologies.service_request_system.model;

import com.genysyxtechnologies.service_request_system.constant.ServiceRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING; // Default value

    @Column(name = "submission_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime submissionDate;

    /**
     * JSON string containing the actual data submitted by the requester, conforming to the
     * structure defined in the associated ServiceOffering's fieldSchema.
     */
    @Column(name = "submitted_data", columnDefinition = "text", nullable = false, length = 10000)
    private String submittedData; // JSON string for submitted data

    private String attachmentUrl;
}
