package com.rescuebites.api.exceptions.custom_exceptions;

/**
 * Excepción lanzada cuando un webhook de Mercado Pago debe ser ignorado.
 * Esto ocurre con webhooks de verificación/ping que no contienen datos válidos.
 *
 * Esta excepción es manejada silenciosamente en el controlador,
 * sin registrar errores en los logs.
 */
public class IgnorableWebhookException extends RuntimeException {

    public IgnorableWebhookException(String message) {
        super(message);
    }

    public IgnorableWebhookException(String message, Throwable cause) {
        super(message, cause);
    }
}
