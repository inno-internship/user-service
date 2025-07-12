package com.innowise.userservice.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing credit card information in API responses.
 * Contains essential card details.
 * 
 * @param id The unique identifier of the card record
 * @param number The card number
 * @param holder The name of the card holder
 * @param expirationDate The date when the card expires
 * @since 1.0
 */
public record CardInfoResponse(
    UUID id,
    String number,
    String holder,
    LocalDate expirationDate
) {} 