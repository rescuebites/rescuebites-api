package com.rescuebites.api.users.utils;

import com.rescuebites.api.users.controllers.requests.RegisterRequest;
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