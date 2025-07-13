package com.innowise.userservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration for data storage and caching capabilities.
 * <p>
 * Provides centralized configuration for Redis operations, defining serialization strategies
 * and connection management for the application's Redis infrastructure.
 * </p>
 *
 * @see RedisTemplate
 * @see RedisConnectionFactory
 * @since 1.0
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a configured Redis template for key-value operations.
     * <p>
     * Configures serialization for both keys (String) and values (JSON),
     * enabling type-safe Redis operations with automatic JSON conversion.
     * </p>
     *
     * @param connectionFactory the auto-configured {@link RedisConnectionFactory}
     * @return a configured {@link RedisTemplate} for Redis operations
     * @see StringRedisSerializer
     * @see GenericJackson2JsonRedisSerializer
     * @since 1.0
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);

        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }

    /**
     * Provides the Redis connection factory for database interactions.
     * <p>
     * Creates a Lettuce-based connection factory with settings from application properties.
     * </p>
     *
     * @return configured {@link LettuceConnectionFactory} for Redis connections
     * @since 1.0
     */
    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
}
