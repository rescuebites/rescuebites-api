package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.data.models.User;

import java.util.UUID;

public interface IEmailService {

    void sendConfirmAccountEmail(User user, UUID token);

    void sendResendConfirmAccountEmail(User user, UUID token);

    void sendResetPasswordEmail(String email, UUID token);

    void sendEmailUpdatedConfirmationEmail(User user, UUID token);
}