package com.innowise.userservice.application.service;

import java.util.List;

public interface CacheService {
    /**
     * Store an object in cache.
     * @param key cache key
     * @param value any Serializable object
     */
    void put(String key, Object value);

    /**
     * Retrieve an object from cache by key and expected type.
     * @param key cache key
     * @param type expected object type
     * @param <T> type parameter
     * @return cached object cast to T, or null if none exists
     */
    <T> T get(String key, Class<T> type);

    /**
     * Retrieve multiple objects from cache by their keys and expected type.
     *
     * @param keys  list of cache keys to retrieve; keys that are not present in the cache will be ignored
     * @param type  expected object type for all retrieved entries
     * @param <T>   type parameter indicating the class of the returned objects
     * @return      a list of cached objects (cast to T) in the same order as the provided keys;
     *              entries corresponding to missing keys will be null
     */
    <T> List<T> multiGet(List<String> keys, Class<T> type);

    /**
     * Remove a specific key from cache.
     * @param key cache key
     */
    void evict(String key);
}
