package com.rescuebites.api.notifications.Interfaces;

import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.product.data.models.Product;

import java.util.UUID;

public interface INotificationService {
    void notifyOrderStatusChange(Order order);

    void notifyNewOrderCommerce(Order order);

    void notifyOrderCanceledCommerce(Order order);

    void notifyProductExpiredProduct(Product product, UUID commerceId);

    void notifyNewOrderClient(Order order);

    void notifyOrderCompleteClient(Order order);

    void notifyOrderReadyClient(Order order);

    void notifyPreparingOrderClient(Order order);

}
