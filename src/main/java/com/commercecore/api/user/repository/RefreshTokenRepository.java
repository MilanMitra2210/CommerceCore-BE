package com.commercecore.api.user.repository;

import com.commercecore.api.user.entity.RefreshToken;
import com.commercecore.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for database operations on the refresh_tokens table.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Looks up a token by its UUID string representation.
     * Used when refreshing access tokens.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes (or revokes) all tokens belonging to a specific user.
     * Useful on password change or forcing a user logout.
     */
    void deleteByUser(User user);

}
