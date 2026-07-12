package com.commercecore.api.user.entity;

import com.commercecore.api.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entity representing a user in the system.
 *
 * <p>Inherits primary key, auditing details, optimistic locking,
 * and soft-delete properties from {@link BaseEntity}.
 *
 * <p>Enforces soft-delete at the Hibernate layer via {@code @SQLRestriction}
 * so queries naturally exclude deleted users.
 */
@Entity
@Table(name = "users")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean blocked = false;

    @Column(name = "block_reason")
    private String blockReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.MANUAL;

    @Column(name = "provider_id")
    private String providerId;

    public enum Role {
        USER, ADMIN
    }

    public enum AuthProvider {
        MANUAL, GOOGLE, APPLE, LINKEDIN, FACEBOOK
    }
}
