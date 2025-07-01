package com.innowise.userservice.application.dto.response;

import java.time.LocalDateTime;

public record ApiError(
    String path,
    String message,
    int statusCode,
    LocalDateTime timestamp
) {} 