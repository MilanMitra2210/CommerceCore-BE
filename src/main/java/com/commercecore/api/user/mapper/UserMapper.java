package com.commercecore.api.user.mapper;

import com.commercecore.api.user.dto.RegisterRequest;
import com.commercecore.api.user.dto.UserDto;
import com.commercecore.api.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct interface to automatically map User Entities to DTOs.
 *
 * <p>Configured with {@code componentModel = "spring"} to automatically
 * generate a Spring-managed Bean that can be constructor-injected.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a User Entity to a UserDto for API output.
     */
    UserDto toDto(User user);

    /**
     * Maps a RegisterRequest input DTO to a User Entity for database insertion.
     *
     * <p>Ignores target fields that aren't mapped directly from input parameters
     * (like passwordHash, which is generated separately via BCrypt, and default active settings).
     */
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "blockReason", ignore = true)
    User toEntity(RegisterRequest request);

}
