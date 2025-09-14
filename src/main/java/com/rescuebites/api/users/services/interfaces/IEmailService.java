package com.rescuebites.api.users.services.interfaces;

import org.springframework.scheduling.annotation.Async;

public interface IEmailService {

    @Async
    void sendEmail(String to, String subject, String htmlContent);
}
