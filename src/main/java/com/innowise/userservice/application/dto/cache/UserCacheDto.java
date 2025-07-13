package com.innowise.userservice.application.dto.cache;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

/**
 * Represents the entire User aggregate, including associated cards, for caching.
 * <p>
 * This Data Transfer Object (DTO) is designed to be stored as a single,
 * self-contained document in the cache. By bundling the user's profile with their
 * list of {@link CardInfoCacheDto}, it ensures data consistency and atomicity
 * for cache reads, preventing scenarios where user data is stale or incomplete.
 *
 * @see com.innowise.userservice.domain.entity.User
 * @see CardInfoCacheDto
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheDto {

    /**
     * The unique identifier for the user.
     */
    private UUID id;

    /**
     * The user's first name.
     */
    private String name;

    /**
     * The user's last name.
     */
    private String surname;

    /**
     * The user's date of birth.
     */
    private LocalDate birthDate;

    /**
     * The user's unique email address.
     */
    private String email;

    /**
     * A list of cached card information associated with this user,
     * allowing the entire aggregate to be cached as one unit.
     */
    private List<CardInfoCacheDto> cards;
}