package com.lokeswarandk.db_backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String error;

    public ResourceNotFoundException(String error, String message) {
        super(message);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public static ResourceNotFoundException forResourceWithId(String resourceName, Object id) {
        return new ResourceNotFoundException(
                resourceName + " not found",
                "No " + resourceName.toLowerCase() + " with id " + id + " exists");
    }
}
