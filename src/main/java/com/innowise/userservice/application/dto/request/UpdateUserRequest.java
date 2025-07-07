package com.innowise.userservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

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