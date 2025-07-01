package com.innowise.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCardInfoRequest(
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}",
            message = "Invalid credit card number")
    String number,
    
    @NotBlank(message = "Card holder name is required")
    @Size(min = 2, max = 100, message = "Card holder name must be between 2 and 100 characters")
    String holder,
    
    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    LocalDate expirationDate
) {} 