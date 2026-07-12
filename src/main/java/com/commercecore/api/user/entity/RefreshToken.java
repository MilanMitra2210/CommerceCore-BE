package com.commercecore.api.user.entity;

import com.commercecore.api.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

/**
 * Entity representing a refresh token used to request new access tokens.
 *
 * <p>Each token belongs to exactly one user.
 *
 * <p><b>JPA design decision:</b>
 * Uses {@code FetchType.LAZY} for the user reference. This means when we load
 * the token to validate it, Hibernate won't perform an expensive SQL JOIN
 * to fetch the user details unless we explicitly call {@code getPlainUser()} in Java.
 */
@Entity
@Table(name = "refresh_tokens")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

}
