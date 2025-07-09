package com.innowise.userservice.application.exception.notfound;

import com.innowise.userservice.application.exception.BaseException;

public class UserNotFoundException extends BaseException {
    private final int statusCode = 404;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
} 