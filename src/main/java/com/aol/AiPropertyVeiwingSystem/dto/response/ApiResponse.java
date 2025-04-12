package com.aol.AiPropertyVeiwingSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("Operation completed successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code("ERROR")
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T errorDetails) {
        return ApiResponse.<T>builder()
                .code("ERROR")
                .message(message)
                .data(errorDetails)
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .code("NOT_FOUND")
                .message(message)
                .data(null)
                .build();
    }
} 