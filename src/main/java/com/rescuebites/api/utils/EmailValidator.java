package com.rescuebites.api.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" // usuario
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*"   // dominio
                    + "(\\.[A-Za-z]{2,})$";               // TLD

    private Pattern pattern;

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}