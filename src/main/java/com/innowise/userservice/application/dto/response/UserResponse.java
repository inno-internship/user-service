package com.innowise.userservice.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String name,
    String surname,
    LocalDate birthDate,
    String email,
    List<CardInfoResponse> cards
) {} 