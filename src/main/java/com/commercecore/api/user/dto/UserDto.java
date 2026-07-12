package com.commercecore.api.user.dto;

import com.commercecore.api.user.entity.User.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing a user profile returned in API responses.
 *
 * <p>This safely hides the hashed password and soft-deleted status
 * from exposing to client portals.
 */
@Getter
@Setter
public class UserDto {

    private UUID id;
    private String email;
    private String name;
    private Role role;
    private boolean active;
    private boolean blocked;
    private String blockReason;
    private String provider;
    private Instant createdAt;
    private Instant updatedAt;

}
