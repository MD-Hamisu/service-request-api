package com.md.service_request_api.exceptions;

import lombok.Getter;

@Getter
public class SynchronizationException extends RuntimeException {
    private final Integer statusCode;

    // Constructor with just a message
    public SynchronizationException(String message) {
        super(message);
        this.statusCode = null;
    }

    // Constructor with a message and HTTP status code
    public SynchronizationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    // Constructor with a message and cause
    public SynchronizationException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    // Constructor with a message, HTTP status code, and cause
    public SynchronizationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
