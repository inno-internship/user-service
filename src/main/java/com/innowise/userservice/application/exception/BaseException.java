package com.innowise.userservice.application.exception;

/**
 * Base exception class for all application-specific exceptions.
 * Provides common functionality for exception handling and HTTP status code mapping.
 *
 * @since 1.0
 */
public abstract class BaseException extends RuntimeException {
    
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message describing the error condition
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the error condition
     * @param cause the underlying cause of this exception
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return the HTTP status code to be returned to the client
     */
    public abstract int getStatusCode();
}
