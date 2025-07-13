package com.innowise.userservice.infrastructure.cache;

import com.innowise.userservice.application.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return type.cast(value);
    }

    @Override
    public <T> List<T> multiGet(List<String> keys, Class<T> type) {
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(value -> value != null ? type.cast(value) : null)
                .toList();
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }
}