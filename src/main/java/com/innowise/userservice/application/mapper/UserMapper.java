package com.innowise.userservice.application.mapper;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

/**
 * Mapper for converting between {@link CreateUserRequest}, {@link UpdateUserRequest}, {@link User}, and {@link UserResponse}.
 *
 * <p>
 * Serves as the bridge between API-layer DTOs and domain-layer entities.
 *
 * @since 1.0
 */
@Mapper(
    componentModel = "spring",
    uses = {CardInfoMapper.class}
)
public interface UserMapper {

    /**
     * Converts {@link CreateUserRequest} to a new {@link User} entity.
     * Incoming request ID and cards list are ignored and managed by persistence layer.
     *
     * @param request the DTO containing user registration data
     * @return a new User entity ready for saving
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(CreateUserRequest request);
    
    /**
     * Partially updates an existing {@link User} entity.
     * Only non-null properties in {@code request} are applied.
     *
     * @param user the existing entity to modify
     * @param request DTO containing fields to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget User user, UpdateUserRequest request);
    
    /**
     * Converts {@link User} entity to {@link UserResponse} DTO for API output.
     *
     * @param user the user entity
     * @return the response DTO with user data and card info
     */
    UserResponse toResponse(User user);
} 