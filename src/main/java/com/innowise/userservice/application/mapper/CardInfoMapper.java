package com.innowise.userservice.application.mapper;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    CardInfo toEntity(CreateCardInfoRequest request, User user);

    CardInfoResponse toResponse(CardInfo cardInfo);
} 