package com.innowise.userservice.application.mapper;

import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.application.dto.cache.UserCacheDto;
import com.innowise.userservice.application.dto.cache.CardInfoCacheDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between domain entities and their cache-specific DTOs.
 * <p>
 * This interface defines the contract for transforming objects from the domain
 * layer (e.g., {@link User}, {@link CardInfo}) into their Data Transfer Object
 * (DTO) counterparts for caching, and vice versa.
 *
 * @since 1.0
 */
@Mapper(componentModel = "spring")
public interface CacheMapper {

    /**
     * Maps a {@link User} domain entity to its cacheable {@link UserCacheDto} representation.
     *
     * @param user The domain entity to convert.
     * @return The DTO ready for caching.
     */
    UserCacheDto toUserCacheDto(User user);

    /**
     * Maps a {@link UserCacheDto} from the cache back to a {@link User} domain entity.
     *
     * @param dto The cached DTO to convert.
     * @return The reconstructed domain entity.
     */
    User toUser(UserCacheDto dto);

    /**
     * Maps a {@link CardInfo} domain entity to its {@link CardInfoCacheDto} representation.
     *
     * @param cardInfo The domain entity to convert.
     * @return The DTO ready for caching.
     */
    CardInfoCacheDto toCardCacheDto(CardInfo cardInfo);

    /**
     * Maps a {@link CardInfoCacheDto} from the cache back to a {@link CardInfo} domain entity.
     *
     * @param dto The cached DTO to convert.
     * @return The reconstructed domain entity.
     */
    @Mapping(target = "user", ignore = true)
    CardInfo toCardInfo(CardInfoCacheDto dto);
}