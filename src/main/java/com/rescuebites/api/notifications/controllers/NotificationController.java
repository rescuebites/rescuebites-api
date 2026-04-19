package com.rescuebites.api.notifications.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.rescuebites.api.notifications.data.models.Notification;
import com.rescuebites.api.notifications.implementations.NotificationServiceImpl;
import com.rescuebites.api.notifications.services.SseService;
import com.rescuebites.api.security.utils.SecurityUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SseService sseService;
    private final NotificationServiceImpl notificationService;

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

    // @GetMapping("/subscribe/commerce")
    // public SseEmitter subscribeCommerce() {
    // UUID commerceId = UUID.fromString("b4b934d1-1ee2-4903-895b-804a93a79d03"); //

    // return sseService.subscribeCommerce(commerceId);
    // }

    @GetMapping("/notifications/unread")
    public List<Notification> getUnread() {
        UUID commerceId = UUID.fromString("b4b934d1-1ee2-4903-895b-804a93a79d03");

        return notificationService.getUnread(commerceId);
    }

    @PatchMapping("/notifications/read/all")
    public void markAllAsRead() {
        UUID userId = UUID.fromString("b4b934d1-1ee2-4903-895b-804a93a79d03");
        notificationService.markAllAsRead(userId);
    }

    @PatchMapping("/notifications/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
    }
}
