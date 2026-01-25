package com.rescuebites.api.users.listeners;

import com.rescuebites.api.users.events.*;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserNotificationListener {

    private final IEmailService emailService;

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendConfirmAccountEmail(event.user(), event.tokenId());
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleResendConfirmation(ResendConfirmationEvent event) {
        emailService.sendResendConfirmAccountEmail(event.user(), event.tokenId());
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordReset(PasswordResetRequestedEvent event) {
        emailService.sendResetPasswordEmail(event.user().getEmail(), event.tokenId());
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailUpdated(EmailUpdatedEvent event) {
        emailService.sendEmailUpdatedConfirmationEmail(event.user(), event.tokenId());
    }
}