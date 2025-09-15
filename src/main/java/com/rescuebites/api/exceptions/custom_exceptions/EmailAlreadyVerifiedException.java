package com.rescuebites.api.exceptions.custom_exceptions;

public class EmailAlreadyVerifiedException extends RuntimeException {

    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }

    public EmailAlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}