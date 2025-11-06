package com.rescuebites.api.users.facades.implementations;

import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.commands.LoginValidationCommand;
import com.rescuebites.api.users.facades.commands.PasswordPairCommand;
import com.rescuebites.api.users.facades.commands.RegistrationValidationCommand;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.facades.policies.AccountStatusPolicy;
import com.rescuebites.api.users.facades.policies.EmailAvailabilityPolicy;
import com.rescuebites.api.users.facades.policies.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade implements IUserFacade {

    private final EmailAvailabilityPolicy emailAvailabilityPolicy;
    private final PasswordPolicy passwordPolicy;
    private final AccountStatusPolicy accountStatusPolicy;

    @Override
    public void validateRegistration(RegistrationValidationCommand command) {
        emailAvailabilityPolicy.ensureAvailable(command.email());
        passwordPolicy.ensureMatch(new PasswordPairCommand(command.password(), command.confirmPassword()));
    }

    @Override
    public void validateLogin(LoginValidationCommand command) {
        passwordPolicy.ensureMatchesStored(command.rawPassword(), command.user());
        accountStatusPolicy.ensureEnabled(command.user());
    }

    @Override
    public void ensureEmailIsAvailable(String email) {
        emailAvailabilityPolicy.ensureAvailable(email);
    }

    @Override
    public void ensureUserIsPendingVerification(User user) {
        accountStatusPolicy.ensurePendingVerification(user);
    }

    @Override
    public void ensurePasswordsMatch(PasswordPairCommand command) {
        passwordPolicy.ensureMatch(command);
    }
}