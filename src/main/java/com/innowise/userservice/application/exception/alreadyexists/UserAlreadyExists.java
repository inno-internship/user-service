package com.innowise.userservice.application.exception.alreadyexists;

import com.innowise.userservice.application.exception.BaseException;

public class UserAlreadyExists extends BaseException {
    
    private final int statusCode = 409;

    public UserAlreadyExists(String message) {
        super(message);
    }

    public UserAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
