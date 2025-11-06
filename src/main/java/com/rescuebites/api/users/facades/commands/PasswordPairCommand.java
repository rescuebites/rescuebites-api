package com.rescuebites.api.users.facades.commands;

public record PasswordPairCommand(
        String password,
        String confirmPassword
) {
}
