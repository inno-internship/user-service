package com.innowise.userservice.application.dto.cache;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a snapshot of a credit card's information for caching purposes.
 * <p>
 * This Data Transfer Object (DTO) is a serializable, lightweight representation
 * of a {@link com.innowise.userservice.domain.entity.CardInfo} entity. It is used
 * to store card data in a cache to improve performance by reducing direct
 - * database queries.
 *
 * @see UserCacheDto
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoCacheDto {

    /**
     * The unique identifier of the card.
     */
    private UUID id;

    /**
     * The card number.
     */
    private String number;

    /**
     * The full name of the cardholder.
     */
    private String holder;

    /**
     * The card's expiration date.
     */
    private LocalDate expirationDate;
}