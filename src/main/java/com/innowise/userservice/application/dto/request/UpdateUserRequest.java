package com.innowise.userservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating existing user information.
 * All fields are optional, allowing partial updates of user data.
 * <p>
 * Null values indicate that the corresponding field should remain unchanged.
 * When provided, values are validated with constraints to ensure data integrity.
 *
 * @param name Optional user's first name, must be between 2 and 100 characters if provided
 * @param surname Optional user's last name, must be between 2 and 100 characters if provided
 * @param birthDate Optional user's date of birth, must be in the past if provided
 * @param email Optional user's email address, must be a valid email format if provided
 * @since 1.0
 */
public record UpdateUserRequest(
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,
    
    @Size(min = 2, max = 100, message = "Surname must be between 2 and 100 characters")
    String surname,
    
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,
    
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email
) {} 