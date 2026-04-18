package com.rescuebites.api.notifications.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.rescuebites.api.notifications.services.SseService;
import com.rescuebites.api.security.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SseService sseService;

    @GetMapping("/subscribe/client")
    public SseEmitter subscribeClient() {
        UUID clientId = SecurityUtils.getAuthenticatedClientId(); // 👈 ya lo tenés
        log.info("CLIENTID", clientId);
        return sseService.subscribeClient(clientId);
    }

    // @GetMapping("/subscribe/commerce")
    // public SseEmitter subscribeCommerce() {
    // UUID commerceId = SecurityUtils.getAuthenticatedCommerceId();
    // log.info("COMMERCEID: {}", commerceId);

    // return sseService.subscribeCommerce(commerceId);
    // }

    @GetMapping("/subscribe/commerce")
    public SseEmitter subscribeCommerce() {
        UUID commerceId = UUID.fromString("b4b934d1-1ee2-4903-895b-804a93a79d03"); // fijo
        return sseService.subscribeCommerce(commerceId);
    }
}
