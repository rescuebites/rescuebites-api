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
    public void sendConfirmAccountEmail(String to, User user, UUID token) {
        String htmlContent = emailBuilder.buildConfirmAccount(user, token);
        sendEmail(to, "Confirm your registration ✔", htmlContent);
    }

    @Override
    public void sendResendConfirmAccountEmail(String to, User user, UUID token) {
        String htmlContent = emailBuilder.buildResendConfirmAccount(user, token);
        sendEmail(to, "Confirm your registration ✔", htmlContent);
    }

    @Override
    public void sendResetPasswordEmail(String to, String email, UUID token) {
        String htmlContent = emailBuilder.buildResetPassword(email, token);
        sendEmail(to, "Reset your password ✔", htmlContent);
    }

    @Override
    public void sendEmailUpdatedConfirmationEmail(String to, String fullName, User user, UUID token) {
        String htmlContent = emailBuilder.buildEmailUpdatedConfirmation(fullName, user, token);
        sendEmail(to, "Email actualizado ✔", htmlContent);
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
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}