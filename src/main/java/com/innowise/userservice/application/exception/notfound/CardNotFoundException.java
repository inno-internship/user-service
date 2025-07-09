package com.innowise.userservice.application.exception.notfound;

import com.innowise.userservice.application.exception.BaseException;

public class CardNotFoundException extends BaseException {
    
    private final int statusCode = 404;

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
} 