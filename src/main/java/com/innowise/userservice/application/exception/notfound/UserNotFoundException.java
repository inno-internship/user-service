package com.innowise.userservice.application.exception.notfound;

import com.innowise.userservice.application.exception.BaseException;

/**
 * Exception thrown when a requested user cannot be found in the system.
 * Maps to HTTP 404 Not Found response.
 *
 * @since 1.0
 */
public class UserNotFoundException extends BaseException {
    private final int statusCode = 404;

    /**
     * Constructs a new exception when a user lookup operation fails.
     *
     * @param message the detail message describing which user was not found
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception when a user lookup operation fails.
     *
     * @param message the detail message describing which user was not found
     * @param cause the underlying cause of the lookup failure
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
} 