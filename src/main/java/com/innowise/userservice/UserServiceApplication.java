package com.innowise.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the User Service.
 * Initializes and bootstraps the Spring application context.
 *
 * @since 1.0
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
