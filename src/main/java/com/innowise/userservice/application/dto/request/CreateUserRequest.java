package com.innowise.userservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating new user accounts.
 * Contains validated personal information required for user registration.
 * <p>
 * All fields are validated with constraints to ensure data integrity.
 * 
 * @param name The user's first name, must be between 2 and 100 characters
 * @param surname The user's last name, must be between 2 and 100 characters
 * @param birthDate The user's date of birth, must be in the past
 * @param email The user's email address, must be a valid email format
 * @since 1.0
 */
public record CreateUserRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,
    
    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 100, message = "Surname must be between 2 and 100 characters")
    String surname,
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email
) {} 