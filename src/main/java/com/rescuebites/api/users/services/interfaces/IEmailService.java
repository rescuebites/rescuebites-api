package com.rescuebites.api.users.services.interfaces;

import com.rescuebites.api.users.data.models.User;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;

public interface IEmailService {

    @Async
    void sendConfirmAccountEmail(String to, User user, UUID token);

    @Async
    void sendResendConfirmAccountEmail(String to, User user, UUID token);

    @Async
    void sendResetPasswordEmail(String to, String email, UUID token);

    @Async
    void sendEmailUpdatedConfirmationEmail(String to, String fullName, User user, UUID token);
}