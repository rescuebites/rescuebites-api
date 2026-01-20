package com.rescuebites.api.users.utils;

import com.rescuebites.api.users.controllers.requests.UserRegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationRequest> {

    @Override
    public boolean isValid(UserRegistrationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }
        return request.password().equals(request.confirmPassword());
    }
}