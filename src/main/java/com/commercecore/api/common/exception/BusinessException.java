package com.commercecore.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract base exception for all business-level exceptions in CommerceCore.
 *
 * <p>Every custom exception extends this class, providing:
 * <ul>
 *     <li>A human-readable message (returned to the client in {@code ApiResponse.detail})</li>
 *     <li>An HTTP status code (used by {@link GlobalExceptionHandler} to set the response status)</li>
 * </ul>
 *
 * <p><b>Why a custom exception hierarchy?</b>
 * <ul>
 *     <li>Maps business errors to HTTP status codes cleanly</li>
 *     <li>Provides a single catch point in {@link GlobalExceptionHandler}</li>
 *     <li>Separates business errors from infrastructure errors (e.g., database connection failures)</li>
 * </ul>
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;

    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    protected BusinessException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
