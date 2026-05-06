package com.rescuebites.api.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailStartupChecks {

    private final Environment env;
    private static final Logger log = LoggerFactory.getLogger(MailStartupChecks.class);

    @PostConstruct
    public void checkMailConfig() {
        String host = env.getProperty("spring.mail.host");
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");
        String frontendUrl = env.getProperty("frontend.url");
        String profile = env.getProperty("spring.profiles.active", "development");

        if (host == null || username == null || password == null) {
            log.warn("Mail properties incomplete on startup: host={}, username={}, passwordSet={}", host, username, password != null);
        } else {
            if (password.contains(" ")) {
                log.warn("Detected whitespace in spring.mail.password. Remove spaces from the password (app passwords must be contiguous).\nValue masked for security.");
            }
        }

        if (frontendUrl != null && frontendUrl.contains("localhost") && !"development".equalsIgnoreCase(profile)) {
            log.warn("frontend.url is set to localhost while active profile is '{}'. Update FRONTEND_URL env var for production.", profile);
        }

        log.info("Startup mail config: host='{}', username='{}', frontend.url='{}', profile='{}'", host, username, frontendUrl, profile);
    }
}
