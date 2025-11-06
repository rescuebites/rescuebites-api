package com.rescuebites.api.users.facades.policies;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.commands.PasswordPairCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class PasswordPolicy {

    private final PasswordEncoder passwordEncoder;

    public void ensureMatch(PasswordPairCommand command) {
        if (!StringUtils.hasText(command.password()) || !StringUtils.hasText(command.confirmPassword())) {
            throw new ValidationException("Debe ingresar y confirmar la contraseña");
        }

        if (!command.password().equals(command.confirmPassword())) {
            throw new ValidationException("Las contraseñas no coinciden");
        }
    }

    public void ensureMatchesStored(String rawPassword, User user) {
        if (!StringUtils.hasText(rawPassword)) {
            throw new ValidationException("Debe ingresar la contraseña");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ValidationException("Contraseña inválida.");
        }
    }
}
