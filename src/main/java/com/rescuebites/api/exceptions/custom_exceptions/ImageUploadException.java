package com.rescuebites.api.exceptions.custom_exceptions;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
