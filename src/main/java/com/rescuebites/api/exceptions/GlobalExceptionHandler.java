package com.rescuebites.api.exceptions;

import com.rescuebites.api.exceptions.custom_exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.error("Data integrity error: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Data integrity error",
                "No se pudo completar la operación debido a un conflicto con los datos existentes"
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Manejo de excepciones para errores inesperados
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error occurred",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Manejo específico de validación
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateResourceException ex) {
        logger.error("Duplicate resource error: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Resource already exists"
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    //Con esto podemos cubrir la parte de id, nombre no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Requested resource not found"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ApiError> handleEmailSending(EmailSendingException ex) {
        logger.error("Email sending error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "Email service failure"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex) {
        logger.error("Token expired error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Verification token has expired"
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex) {
        logger.error("Email already verified error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Email has already been verified"
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiError> handleTooManyRequests(TooManyRequestsException ex) {
        logger.error("Too many requests error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ex.getMessage(),
                "Rate limit exceeded"
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ApiError> handleImageUpload(ImageUploadException ex) {
        logger.error("Image upload error: {}", ex.getMessage(), ex);
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "Image upload failed"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<ApiError> handlePasswordsDoNotMatch(PasswordsDoNotMatchException ex) {
        logger.error("Passwords do not match error: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Password validation failed"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex) {
        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                "You´re not authorized to access to this resource"
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiError> handlePaymentException(PaymentException ex) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Payment processing failed"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWebhookSignatureException.class)
    public ResponseEntity<ApiError> handleInvalidWebhookSignature(InvalidWebhookSignatureException ex) {
        logger.warn("Invalid webhook signature: {}", ex.getMessage());
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Webhook signature validation failed"
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CommerceClosedReopensException.class)
    public ResponseEntity<CommerceClosedError> handleCommerceClosedReopens(CommerceClosedReopensException ex) {
        CommerceClosedError error = new CommerceClosedError(
                HttpStatus.CONFLICT.value(),
                "Commerce closed",
                ex.getMessage(),
                ex.getNextOpenTime(),
                ex.getNextCloseTime()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}