package com.innowise.userservice.application.exception;

import java.util.List;
import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    private final List<UUID> notFoundIds;

    public UserNotFoundException(List<UUID> notFoundIds) {
        super("User not found with id: " + notFoundIds);
        this.notFoundIds = notFoundIds;
    }

    public UserNotFoundException(UUID id) {
        this(List.of(id));
    }

    public UserNotFoundException(String message) {
        super(message);
        this.notFoundIds = List.of();
    }

    public List<UUID> getNotFoundIds() {
        return notFoundIds;
    }
} 