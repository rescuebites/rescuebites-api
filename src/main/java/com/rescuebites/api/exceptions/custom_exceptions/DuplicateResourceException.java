package com.rescuebites.api.exceptions.custom_exceptions;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resource, String field) {
        super(String.format("El %s con este %s ya existe", resource, field));
    }
    public DuplicateResourceException(String resource, String field, Throwable cause) {
        super(String.format("El %s con este %s ya existe", resource, field), cause);
    }
}