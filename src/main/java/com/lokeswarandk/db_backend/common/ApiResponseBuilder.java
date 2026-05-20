package com.lokeswarandk.db_backend.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ApiResponseBuilder {

    private static final String KEY_TIMESTAMP = "timestamp";

    private ApiResponseBuilder() {
        // Utility class.
    }

    public static ResponseEntity<Object> error(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_TIMESTAMP, LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Object> validationError(Map<String, String> details) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_TIMESTAMP, LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static Map<String, Object> messagePayload(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_TIMESTAMP, LocalDateTime.now());
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> messagePayload(String message, String key, Object value) {
        Map<String, Object> response = messagePayload(message);
        response.put(key, value);
        return response;
    }
}
