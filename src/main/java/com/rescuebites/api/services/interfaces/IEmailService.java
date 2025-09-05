package com.rescuebites.api.services.interfaces;

import org.springframework.scheduling.annotation.Async;

public interface IEmailService {

    @Async
    void sendEmail(String to, String subject, String htmlContent);
}
