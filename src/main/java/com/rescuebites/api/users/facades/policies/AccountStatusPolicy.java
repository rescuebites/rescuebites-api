package com.rescuebites.api.users.facades.policies;

import com.rescuebites.api.exceptions.custom_exceptions.EmailAlreadyVerifiedException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

@Component
public class AccountStatusPolicy {

    public void ensureEnabled(User user) {
        if (!user.isEnabled()) {
            throw new ValidationException("Debes confirmar tu cuenta antes de iniciar sesión");
        }
    }

    public void ensurePendingVerification(User user) {
        if (user.isEnabled()) {
            throw new EmailAlreadyVerifiedException("El usuario ya ha sido verificado");
        }
    }
}
