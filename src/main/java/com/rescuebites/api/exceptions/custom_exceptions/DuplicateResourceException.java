package com.rescuebites.api.exceptions.custom_exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resource, String field) {
        super(String.format("%s with this %s already exists", resource, field));
    }
    public DuplicateResourceException(String resource, String field, Throwable cause) {
        super(String.format("%s with this %s already exists", resource, field), cause);
    }
}