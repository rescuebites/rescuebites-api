package com.rescuebites.api.notifications.services.interfaces;

import com.rescuebites.api.notifications.data.models.Notification;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.product.data.models.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface INotificationService {

    void notifyOrderStatusChange(Order order, OrderStatus newStatus);

    void notifyNewOrderCommerce(Order order);

    void notifyNewOrderCommerceScheduled(Order order, LocalDateTime scheduledFor);

    void notifyOrderCanceledCommerce(Order order);

    void notifyProductExpiredProduct(Product product, UUID commerceId);

    List<Notification> getUnread(UUID userId);

    void markAllAsRead(UUID userId);

    void markAsRead(UUID id);
}
