package com.rescuebites.api.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment.redirect")
public class PaymentRedirectConfig {
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
}
