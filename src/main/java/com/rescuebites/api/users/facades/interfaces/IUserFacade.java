package com.rescuebites.api.users.facades.interfaces;

import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.commands.LoginValidationCommand;
import com.rescuebites.api.users.facades.commands.PasswordPairCommand;
import com.rescuebites.api.users.facades.commands.RegistrationValidationCommand;

public interface IUserFacade {

    void validateRegistration(RegistrationValidationCommand command);

    void validateLogin(LoginValidationCommand command);

    void ensureEmailIsAvailable(String email);

    void ensureUserIsPendingVerification(User user);

    void ensurePasswordsMatch(PasswordPairCommand command);
}