package com.commercecore.api.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a request is invalid due to business logic rules.
 *
 * <p>Maps to HTTP 400 Bad Request.
 *
 * <p>This is different from Bean Validation failures (which are handled separately).
 * Use this for business-level validation that goes beyond field-level constraints.
 *
 * <p>Usage:
 * <pre>{@code
 * if (order.getStatus() == OrderStatus.SHIPPED) {
 *     throw new BadRequestException("Cannot cancel an order that has already shipped");
 * }
 * }</pre>
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }

}
