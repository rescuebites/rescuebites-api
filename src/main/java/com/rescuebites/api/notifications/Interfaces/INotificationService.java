package com.rescuebites.api.notifications.Interfaces;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.product.data.models.Product;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rescuebites.api.order.data.enums.OrderStatus;

public interface INotificationService {
    void notifyOrderStatusChange(Order order, OrderStatus newStatus);

    void notifyNewOrderCommerce(Order order);

    void notifyNewOrderCommerceScheduled(Order order, LocalDateTime scheduledFor);

    void notifyOrderCanceledCommerce(Order order);

    void notifyProductExpiredProduct(Product product, UUID commerceId);
}
