package com.innowise.userservice.application.dto.response;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an API error response.
 * Used to provide structured error information when API requests fail.
 * 
 * @param path The URI path where the error occurred
 * @param message A descriptive message explaining the error
 * @param statusCode The HTTP status code associated with the error
 * @param timestamp The date and time when the error occurred
 * @since 1.0
 */
public record ApiError(
    String path,
    String message,
    int statusCode,
    LocalDateTime timestamp
) {} 