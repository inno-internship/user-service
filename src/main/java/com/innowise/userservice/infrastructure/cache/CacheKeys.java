package com.innowise.userservice.infrastructure.cache;

import java.util.UUID;

/**
 * A utility class for generating standardized cache keys.
 * <p>
 * Provides a centralized and consistent way to create keys for objects stored
 * in the cache. Using this class prevents key-naming conflicts and simplifies
 * key management across the application.
 *
 * @since 1.0
 */
public final class CacheKeys {

    /**
     * The standard prefix for cache keys that store user aggregate data.
     */
    public static final String USER_WITH_CARDS_PREFIX = "user:with:cards:";

    /**
     * Generates the specific cache key for a user aggregate.
     *
     * @param userId The unique identifier of the user.
     * @return A consistent, formatted string to be used as a cache key.
     */
    public static String getUserWithCardsKey(UUID userId) {
        return USER_WITH_CARDS_PREFIX + userId;
    }

    private CacheKeys() {}
}