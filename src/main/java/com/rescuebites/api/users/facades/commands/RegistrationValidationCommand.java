package com.rescuebites.api.users.facades.commands;

public record RegistrationValidationCommand(
        String email,
        String password,
        String confirmPassword
) {
}
