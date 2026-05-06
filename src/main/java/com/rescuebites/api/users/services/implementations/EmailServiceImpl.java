package com.rescuebites.api.users.services.implementations;

import com.rescuebites.api.exceptions.custom_exceptions.EmailSendingException;
import com.rescuebites.api.shared.EmailBuilder;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    private final EmailBuilder emailBuilder;

    @Override
    public void sendConfirmAccountEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildConfirmAccount(user, token);
        sendEmail(user.getEmail(), "Confirmá tu registro en RescueBites ✔", htmlContent);
    }

    @Override
    public void sendResendConfirmAccountEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildResendConfirmAccount(user, token);
        sendEmail(user.getEmail(), "Nuevo enlace de confirmación ✔", htmlContent);
    }

    @Override
    public void sendResetPasswordEmail(String email, UUID token) {
        String htmlContent = emailBuilder.buildResetPassword(email, token);
        sendEmail(email, "Restablecé tu contraseña ✔", htmlContent);
    }

    @Override
    public void sendEmailUpdatedConfirmationEmail(User user, UUID token) {
        String htmlContent = emailBuilder.buildEmailUpdatedConfirmation(user, token);
        sendEmail(user.getEmail(), "Confirmá tu nuevo correo electrónico ✔", htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Preparing to send email to {} with subject={}", to, subject);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {} subject {}: {}", to, subject, e.getMessage(), e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }
}