package com.rescuebites.api.exceptions.custom_exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s con %s '%s' no registrado", resource, field, value));
    }

    public ResourceNotFoundException(String resource, String field, Object value, Throwable cause) {
        super(String.format("%s con %s '%s' no registrado", resource, field, value), cause);
    }
}
