package com.innowise.userservice.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record CardInfoResponse(
    UUID id,
    String number,
    String holder,
    LocalDate expirationDate
) {} 