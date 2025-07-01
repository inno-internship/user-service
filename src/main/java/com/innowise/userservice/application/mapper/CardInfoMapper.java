package com.innowise.userservice.application.mapper;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", source = "user")
    CardInfo toEntity(CreateCardInfoRequest request, UUID id, User user);
    
    CardInfoResponse toResponse(CardInfo cardInfo);
} 