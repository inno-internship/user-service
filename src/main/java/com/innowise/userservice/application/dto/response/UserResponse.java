package com.innowise.userservice.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a user's information in API responses.
 * Contains complete user profile data including associated credit cards.
 * <p>
 * This DTO is typically used for retrieving user details and their linked payment methods.
 *
 * @param id The unique identifier of the user
 * @param name The user's first name
 * @param surname The user's last name
 * @param birthDate The user's date of birth
 * @param email The user's email address
 * @param cards List of credit cards associated with the user
 * @since 1.0
 */
public record UserResponse(
    UUID id,
    String name,
    String surname,
    LocalDate birthDate,
    String email,
    List<CardInfoResponse> cards
) {} 