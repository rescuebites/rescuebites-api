package com.rescuebites.api.notifications.Interfaces;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.product.data.models.Product;

import java.util.UUID;

public interface INotificationService {
    void notifyOrderStatusChange(Order order);
    void notifyNewOrder(Order order);
    void notifyProductOutOfStock(Product product, UUID commerceId);
}
