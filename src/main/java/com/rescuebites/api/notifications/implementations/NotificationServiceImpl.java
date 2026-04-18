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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final SseService sseService; // 👈 tu manejador de emitters
    private final NotificationRepository notificationRepository;

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
    public void notifyNewOrderCommerce(Order order) {
        log.info("NUEVA NOTIFICACION", order.getOrderId());
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToCommerce(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyProductExpiredProduct(Product product, UUID commerceId) {
        Map<String, Object> payload = Map.of(
                "type", "OUT_OF_STOCK",
                "productId", product.getProductId(),
                "name", product.getName());

        sseService.sendToCommerce(commerceId, payload);
        saveNotification(commerceId, payload);
    }

    @Override
    public void notifyOrderCanceledCommerce(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToClient(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyNewOrderClient(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToClient(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyOrderCompleteClient(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToClient(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyOrderReadyClient(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToClient(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    @Override
    public void notifyPreparingOrderClient(Order order) {
        Map<String, Object> payload = Map.of(
                "type", "Confirmed",
                "orderId", order.getOrderId(),
                "message", "We´ve receive your order and payment. You´ll get another update soon.");

        sseService.sendToClient(order.getCommerce().getCommerceId(), payload);
        saveNotification(order.getCommerce().getCommerceId(), payload);
    }

    private void saveNotification(UUID userId, Map<String, Object> payload) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type((String) payload.get("type"))
                .title("Pedido confirmado")
                .message("Ha recibido un nuevo pedido")
                .data(payload.toString()) // después podés serializar a JSON
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

}
