package com.innowise.userservice.application.mapper;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between {@link CreateCardInfoRequest}, {@link CardInfo}, and {@link CardInfoResponse}.
 *
 * <p>
 * Serves as the bridge between API-layer DTOs and domain-layer entities.
 *
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    /**
     * Maps API request to a new {@link CardInfo} entity.
     *
     * @param request the DTO containing card details
     * @param user the owning user entity
     * @return a new CardInfo entity (id is ignored/set by persistence)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    CardInfo toEntity(CreateCardInfoRequest request, User user);

    /**
     * Maps {@link CardInfo} entity to API response DTO.
     *
     * @param cardInfo the card information entity
     * @return the response object to be sent to clients
     */
    CardInfoResponse toResponse(CardInfo cardInfo);
} 