package com.rescuebites.api.notifications.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.rescuebites.api.notifications.services.SseService;
import com.rescuebites.api.security.utils.SecurityUtils;


@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SseService sseService;

    @GetMapping("/subscribe/client")
    public SseEmitter subscribeClient() {
        UUID clientId = SecurityUtils.getAuthenticatedClientId(); // 👈 ya lo tenés
        return sseService.subscribeClient(clientId);
    }

    @GetMapping("/subscribe/commerce")
    public SseEmitter subscribeCommerce() {
        UUID commerceId = SecurityUtils.getAuthenticatedCommerceId();
        return sseService.subscribeCommerce(commerceId);
    }
}
