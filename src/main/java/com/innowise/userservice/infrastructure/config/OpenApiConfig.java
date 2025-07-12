package com.innowise.userservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI Swagger documentation.
 * <p>
 * Sets up the OpenAPI specification for the User Service API, including title and version information.
 * </p>
 *
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Defines the OpenAPI bean for the User Service API.
     * <p>
     * Configures the API documentation with title and version metadata.
     * </p>
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0"));
    }
} 