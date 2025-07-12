package com.innowise.userservice.application.exception.notfound;

import com.innowise.userservice.application.exception.BaseException;

/**
 * Exception thrown when a requested credit card cannot be found in the system.
 * Maps to HTTP 404 Not Found response.
 *
 * @since 1.0
 */
public class CardNotFoundException extends BaseException {
    
    private final int statusCode = 404;

    /**
     * Constructs a new exception when a card lookup operation fails.
     *
     * @param message the detail message describing which card was not found
     */
    public CardNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception when a card lookup operation fails.
     *
     * @param message the detail message describing which card was not found
     * @param cause the underlying cause of the lookup failure
     */
    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
} 