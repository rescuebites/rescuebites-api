package com.rescuebites.api.utils;

import com.rescuebites.api.controllers.requests.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }
        return request.password().equals(request.confirmPassword());
    }
}