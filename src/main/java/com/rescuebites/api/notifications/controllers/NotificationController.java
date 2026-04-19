package com.rescuebites.api.notifications.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.rescuebites.api.notifications.data.models.Notification;
import com.rescuebites.api.notifications.implementations.NotificationServiceImpl;
import com.rescuebites.api.notifications.services.SseService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SseService sseService;
    private final NotificationServiceImpl notificationService;

    @GetMapping("/subscribe/client")
    public SseEmitter subscribeClient(@RequestParam UUID clientId) {
        return sseService.subscribeClient(clientId);
    }

    @GetMapping("/subscribe/commerce")
    public SseEmitter subscribeCommerce(@RequestParam UUID commerceId) {
        return sseService.subscribeCommerce(commerceId);
    }

    @GetMapping("/notifications/unread")
    public List<Notification> getUnread(@RequestParam UUID userId) {
        return notificationService.getUnread(userId);
    }

    @PatchMapping("/notifications/read/all")
    public void markAllAsRead(@RequestParam UUID userId) {
        notificationService.markAllAsRead(userId);
    }

    @PatchMapping("/notifications/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
    }
}
