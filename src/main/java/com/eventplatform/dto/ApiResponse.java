package com.eventplatform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Unified API Response wrapper for all endpoints
 * Ensures consistent response format across the application
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // Status codes: SUCCESS, ERROR, VALIDATION_ERROR, NOT_FOUND, UNAUTHORIZED
    private String status;

    // HTTP status code (200, 400, 401, 404, 500, etc.)
    private Integer code;

    // Main data payload
    private T data;

    // Success or error message
    private String message;

    // Timestamp of the response
    private LocalDateTime timestamp;

    // For validation errors: field-level error details
    private Map<String, String> errors;

    /**
     * Success response with data
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .code(200)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Success response with custom HTTP code
     */
    public static <T> ApiResponse<T> success(T data, String message, Integer code) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .code(code)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Error response
     */
    public static <T> ApiResponse<T> error(String status, Integer code, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Validation error response with field-level errors
     */
    public static <T> ApiResponse<T> validationError(Map<String, String> errors, String message) {
        return ApiResponse.<T>builder()
                .status("VALIDATION_ERROR")
                .code(400)
                .errors(errors)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Not found error
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .status("NOT_FOUND")
                .code(404)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Unauthorized error
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .status("UNAUTHORIZED")
                .code(401)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
