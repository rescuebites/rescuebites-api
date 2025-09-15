package com.rescuebites.api.exceptions.custom_exceptions;

/*
    * Custom exception for handling email sending errors.
    * This exception can be thrown when there is an issue with sending an email,
    * such as invalid email format, SMTP server issues, etc.
*/
/*
 Hereda de RuntimeException, lo que significa que es una excepción no comprobada (unchecked):
    No estás obligado a declararla con throws.
    No tenés que capturarla con try-catch si no querés
*/
public class MessagingException extends RuntimeException {
    public MessagingException(String message) {
        super(message);
    }
}
