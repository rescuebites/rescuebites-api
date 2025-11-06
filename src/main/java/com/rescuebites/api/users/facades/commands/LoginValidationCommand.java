package com.rescuebites.api.users.facades.commands;

import com.rescuebites.api.users.data.models.User;

public record LoginValidationCommand(
        String rawPassword,
        User user
) {
}
