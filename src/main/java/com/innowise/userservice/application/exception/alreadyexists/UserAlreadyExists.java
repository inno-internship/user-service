package com.innowise.userservice.application.exception.alreadyexists;

import com.innowise.userservice.application.exception.BaseException;

/**
 * Exception thrown when attempting to create a user that already exists.
 * Maps to HTTP 409 Conflict response.
 *
 * @since 1.0
 */
public class UserAlreadyExists extends BaseException {
    
    private final int statusCode = 409;

    /**
     * Constructs a new exception when a user creation fails due to duplicate data.
     *
     * @param message the detail message describing the conflict
     */
    public UserAlreadyExists(String message) {
        super(message);
    }

    /**
     * Constructs a new exception when a user creation fails due to duplicate data.
     *
     * @param message the detail message describing the conflict
     * @param cause the underlying cause of the creation failure
     */
    public UserAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
