package com.commercecore.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Unified API response wrapper used by every endpoint in the application.
 *
 * <p>This contract matches the frontend's expected format exactly:
 * <pre>{@code
 * {
 *   "success": true,
 *   "detail": "Products retrieved successfully",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p><b>Why a unified response wrapper?</b>
 * <ul>
 *     <li>Consistency: every API returns the same structure, frontend parsing is trivial</li>
 *     <li>Error handling: errors follow the same format, with {@code success: false}</li>
 *     <li>Contract stability: the outer structure never changes, even as inner data evolves</li>
 * </ul>
 *
 * @param <T> the type of the {@code data} payload
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String detail;
    private T data;

    // ---- Static Factory Methods ----

    /**
     * Creates a success response with data and a detail message.
     */
    public static <T> ApiResponse<T> success(String detail, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .detail(detail)
                .data(data)
                .build();
    }

    /**
     * Creates a success response with data and a default detail message.
     */
    public static <T> ApiResponse<T> success(T data) {
        return success("Operation completed successfully", data);
    }

    /**
     * Creates a success response with only a detail message (no data payload).
     */
    public static <T> ApiResponse<T> success(String detail) {
        return ApiResponse.<T>builder()
                .success(true)
                .detail(detail)
                .build();
    }

    /**
     * Creates an error response with a detail message and optional error data.
     */
    public static <T> ApiResponse<T> error(String detail, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .detail(detail)
                .data(data)
                .build();
    }

    /**
     * Creates an error response with only a detail message.
     */
    public static <T> ApiResponse<T> error(String detail) {
        return ApiResponse.<T>builder()
                .success(false)
                .detail(detail)
                .build();
    }

}
