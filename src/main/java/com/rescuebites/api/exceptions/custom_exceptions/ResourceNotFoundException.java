package com.rescuebites.api.exceptions.custom_exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s with %s '%s' not found", resource, field, value));
    }

    public ResourceNotFoundException(String resource, String field, Object value, Throwable cause) {
        super(String.format("%s with %s '%s' not found", resource, field, value), cause);
    }
}
