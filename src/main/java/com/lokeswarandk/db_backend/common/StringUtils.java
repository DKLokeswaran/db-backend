package com.lokeswarandk.db_backend.common;

public final class StringUtils {

    private StringUtils() {
        // Utility class.
    }

    public static String requireNonBlank(String value, String paramName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value.trim();
    }
}
