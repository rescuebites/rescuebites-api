package com.rescuebites.api.notifications.implementations;

import com.rescuebites.api.order.data.models.Order;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;

import com.rescuebites.api.notifications.Interfaces.INotificationService;
import com.rescuebites.api.notifications.data.models.Notification;
import com.rescuebites.api.notifications.repositories.NotificationRepository;
import com.rescuebites.api.notifications.services.SseService;
import com.rescuebites.api.product.data.models.Product;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rescuebites.api.order.data.enums.OrderStatus;

import org.springframework.scheduling.annotation.Scheduled;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final SseService sseService;
    private final NotificationRepository notificationRepository;

    @Override
    public void notifyOrderStatusChange(Order order, OrderStatus newStatus) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", order.getOrderNumber());
        payload.put("registerId", order.getOrderId().toString());

        switch (newStatus) {
            case PENDING -> {
                payload.put("type", "PENDING");
                payload.put("message", "Tu pedido fue creado");
            }
            case CONFIRMED -> {
                payload.put("type", "CONFIRMED");
                payload.put("message", "Tu pedido fue confirmado");
            }
            case PREPARING -> {
                payload.put("type", "PREPARING");
                payload.put("message", "El comercio está preparando tu pedido");
            }
            case READY -> {
                payload.put("type", "READY");
                payload.put("message", "Tu pedido está listo para retirar");
            }
            case COMPLETED -> {
                payload.put("type", "COMPLETED");
                payload.put("message", "Pedido completado");
            }
            case CANCELLED -> {
                payload.put("type", "CANCELED");
                payload.put("message", "Pedido cancelado");
            }
        }

        // CLIENTE
        sseService.sendToClient(order.getClient().getClientId(), payload);

        // Persistencia
        saveNotification(order.getClient().getClientId(), payload);
    }

    @Override
    public void notifyNewOrderCommerce(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "CONFIRMED",
                "eventId", order.getOrderNumber(),
                "registerId", order.getOrderId().toString(),
                "notes", order.getNotes() != null ? order.getNotes() : "",
                "message", "Ha recibido un nuevo pedido.");

        sseService.sendToCommerce(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyNewOrderCommerceScheduled(Order order, LocalDateTime scheduledFor) {
        Map<String, Object> payload = Map.of(
                "type", "CONFIRMED",
                "eventId", order.getOrderNumber(),
                "registerId", order.getOrderId().toString(),
                "notes", order.getNotes() != null ? order.getNotes() : "",
                "message", "Ha recibido un nuevo pedido.");

        // La notificación no se enviará hasta que reabra el comercio
        saveNotification(order.getCommerce().getCommerceId(), payload, scheduledFor);
    }

    @Override
    public void notifyProductExpiredProduct(Product product, UUID commerceId) {
        Map<String, Object> payload = Map.of(
                "type", "OUT_OF_STOCK",
                "eventId", product.getProductId(),
                "name", product.getName());

        sseService.sendToCommerce(commerceId, payload);
        saveNotification(commerceId, payload);
    }

    @Override
    public void notifyOrderCanceledCommerce(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "CANCELED",
                "eventId", order.getOrderNumber().toString(),
                "registerId", order.getOrderId().toString(),
                "notes", order.getNotes() != null ? order.getNotes() : "",
                "message", "Pedido cancelado.");

        sseService.sendToCommerce(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    private void saveNotification(UUID userId, Map<String, Object> payload) {
        saveNotification(userId, payload, null);
    }

    private void saveNotification(UUID userId, Map<String, Object> payload, LocalDateTime scheduledFor) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type((String) payload.get("type"))
                .message((String) payload.get("message"))
                .title("Nueva notificación")
                .eventId((String) payload.get("eventId"))
                .registerId((String) payload.get("registerId"))
                .data(payload.toString())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .notes((String) payload.get("notes"))
                .scheduledFor(scheduledFor)
                .build();

        notificationRepository.save(notification);
    }

    public List<Notification> getUnread(UUID userId) {
        return notificationRepository.findVisibleUnreadByUserId(userId, LocalDateTime.now());
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void markAsRead(UUID id) {
        notificationRepository.markAsRead(id);
    }

    // Este método se ejecutará cada minuto para entregar notificaciones programadas
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void deliverPendingNotifications() {
        List<Notification> due = notificationRepository.findDueScheduledNotifications(LocalDateTime.now());
        for (Notification notification : due) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", notification.getType());
            payload.put("eventId", notification.getEventId());
            payload.put("registerId", notification.getRegisterId());
            payload.put("notes", notification.getNotes() != null ? notification.getNotes() : "");
            payload.put("message", notification.getMessage());
            sseService.sendToCommerce(notification.getUserId(), payload);
            notification.setScheduledFor(null);
            notificationRepository.save(notification);
        }
    }
}