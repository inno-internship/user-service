package com.innowise.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for creating new card information records.
 * Contains validated credit card details associated with a specific user.
 * 
 * @param userId The unique identifier of the user who owns the card
 * @param number The 16-digit credit card number
 * @param holder The full name of the card's holder as it appears on the card
 * @param expirationDate The date when the card expires
 * @since 1.0
 */
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