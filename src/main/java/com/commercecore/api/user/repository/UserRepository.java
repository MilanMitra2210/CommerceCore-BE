package com.commercecore.api.user.repository;

import com.commercecore.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for database operations on the users table.
 *
 * <p>Extends {@link JpaRepository} to inherit all default CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by email.
     * Used during login and registration lookup.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email.
     * Used for registration validation.
     */
    boolean existsByEmail(String email);

}
