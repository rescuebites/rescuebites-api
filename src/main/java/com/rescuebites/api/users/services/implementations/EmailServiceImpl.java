package com.rescuebites.api.users.services.implementations;

import com.rescuebites.api.shared.EmailBuilder;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final EmailBuilder emailBuilder;

    @Override
    public void sendConfirmAccountEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildConfirmAccount(user, token);
        sendEmail(user.getEmail(),"Confirm your registration ✔", htmlContent);
    }

    @Override
    public void sendResendConfirmAccountEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildResendConfirmAccount(user, token);
        sendEmail(user.getEmail(), "Confirm your registration ✔", htmlContent);
    }

    @Override
    public void sendResetPasswordEmail(String email, UUID token) {
        String htmlContent = emailBuilder.buildResetPassword(email, token);
        sendEmail(email, "Reset your password ✔", htmlContent);
    }

    @Override
    public void sendEmailUpdatedConfirmationEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildEmailUpdatedConfirmation(user, token);
        // Enviamos al nuevo email pendiente (si existe), de lo contrario al email actual
        String recipient = (user.getPendingEmail() != null && !user.getPendingEmail().isBlank())
                ? user.getPendingEmail()
                : user.getEmail();
        sendEmail(recipient, "Confirma tu nuevo email ✔", htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}