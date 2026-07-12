package com.commercecore.api.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Thrown when a requested resource cannot be found.
 *
 * <p>Maps to HTTP 404 Not Found.
 *
 * <p>Usage:
 * <pre>{@code
 * Product product = productRepository.findById(id)
 *     .orElseThrow(() -> new ResourceNotFoundException("Product", id));
 * }</pre>
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " with id " + id + " not found", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " with " + fieldName + " '" + fieldValue + "' not found", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
