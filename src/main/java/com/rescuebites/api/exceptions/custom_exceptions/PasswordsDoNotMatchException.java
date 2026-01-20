package com.rescuebites.api.exceptions.custom_exceptions;

public class PasswordsDoNotMatchException extends RuntimeException {

    public PasswordsDoNotMatchException() {
        super("Las contraseñas no coinciden");
    }

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}