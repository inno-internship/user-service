package com.innowise.userservice.presentation.exception;

import com.innowise.userservice.application.dto.response.ApiError;
import com.innowise.userservice.application.exception.BaseException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API controllers.
 * 
 * Intercepts and processes exceptions, mapping them to standardized API error responses ({@link ApiError}).
 * Handles application-specific exceptions, validation errors, and unexpected system errors, returning appropriate HTTP status codes and error details in the response body.
 * 
 * Maps exceptions to the following HTTP status codes:
 * <ul>
 *   <li>400 Bad Request — for validation errors, unreadable messages, and constraint violations</li>
 *   <li>404 Not Found — for application-specific not found exceptions</li>
 *   <li>409 Conflict — for already exists exceptions</li>
 *   <li>500 Internal Server Error — for unexpected errors</li>
 * </ul>
 *
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles application-specific exceptions derived from {@link BaseException}.
     *
     * @param ex the application exception
     * @param request the current web request
     * @return standardized API error response as {@link ApiError} with the exception's HTTP status code
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException ex, WebRequest request) {
        log.error("Service exception occurred: {}", ex.getMessage());

        ApiError error = new ApiError(
                request.getDescription(false),
                ex.getMessage(),
                ex.getStatusCode(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatusCode()));
    }

    /**
     * Handles validation errors for method arguments annotated with {@code @Valid}.
     *
     * @param ex the validation exception
     * @param request the current web request
     * @return API error response as {@link ApiError} with HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(error ->
                        error.getField() + ": " + error.getDefaultMessage()).
                collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", message);

        ApiError error = new ApiError(
                request.getDescription(false),
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors for handler method parameters.
     *
     * @param ex the handler method validation exception
     * @param request the current web request
     * @return API error response as {@link ApiError} with HTTP 400 Bad Request
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidationException(HandlerMethodValidationException ex, WebRequest request) {
        String message = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Handler method validation failed: {}", message);

        ApiError error = new ApiError(
                request.getDescription(false),
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles cases where the HTTP request body is unreadable or malformed.
     *
     * @param ex the message not readable exception
     * @param request the current web request
     * @return API error response as {@link ApiError} with HTTP 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Message not readable: {}", ex.getMessage());

        ApiError error = new ApiError(
                request.getDescription(false),
                "Unreadable message: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles constraint violations for request parameters and path variables.
     *
     * @param ex the constraint violation exception
     * @param request the current web request
     * @return API error response as {@link ApiError} with HTTP 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations().stream().map(violation ->
                        violation.getPropertyPath() + ": " + violation.getMessage()).
                collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", message);

        ApiError error = new ApiError(
                request.getDescription(false),
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all unexpected exceptions not explicitly handled elsewhere.
     *
     * @param ex the unexpected exception
     * @param request the current web request
     * @return API error response as {@link ApiError} with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        
        ApiError error = new ApiError(
                request.getDescription(false),
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 