package com.commercecore.api.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to create a resource that already exists.
 *
 * <p>Maps to HTTP 409 Conflict.
 *
 * <p>Usage:
 * <pre>{@code
 * if (userRepository.existsByEmail(email)) {
 *     throw new DuplicateResourceException("User", "email", email);
 * }
 * }</pre>
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " with " + fieldName + " '" + fieldValue + "' already exists", HttpStatus.CONFLICT);
    }

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
