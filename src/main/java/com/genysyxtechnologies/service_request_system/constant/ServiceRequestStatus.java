package com.genysyxtechnologies.service_request_system.constant;

public enum ServiceRequestStatus {
    PENDING("Awaiting processing"),
    IN_PROGRESS("Being worked on"),
    COMPLETED("Finished");

    private final String description;

    ServiceRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
