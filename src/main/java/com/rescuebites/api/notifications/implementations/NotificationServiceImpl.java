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

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final SseService sseService; // 👈 tu manejador de emitters
    private final  NotificationRepository notificationRepository;

    @Override
    public void notifyOrderStatusChange(Order order) {

        Map<String, Object> payload = Map.of(
                "type", "ORDER_STATUS",
                "orderId", order.getOrderId(),
                "status", order.getStatus());

        // 🔔 CLIENTE
        sseService.sendToClient(order.getClient().getClientId(), payload);

        // 🔔 COMERCIO
        sseService.sendToCommerce(order.getCommerce().getCommerceId(), payload);

        // 💾 persistencia (clave)
        saveNotification(order.getClient().getClientId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyNewOrder(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "NEW_ORDER",
                "orderId", order.getOrderId());

        sseService.sendToCommerce(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyProductOutOfStock(Product product, UUID commerceId) {
        Map<String, Object> payload = Map.of(
                "type", "OUT_OF_STOCK",
                "productId", product.getProductId(),
                "name", product.getName());

        sseService.sendToCommerce(commerceId, payload);
        saveNotification(commerceId, payload);
    }

    private void saveNotification(UUID userId, Map<String, Object> payload) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type((String) payload.get("type"))
                .title("Actualización de pedido")
                .message("Hubo un cambio en tu pedido")
                .data(payload.toString()) // después podés serializar a JSON
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
}
