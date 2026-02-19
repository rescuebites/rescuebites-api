package com.rescuebites.api.shared.services.interfaces;

import com.rescuebites.api.order.data.models.Order;

public interface IWhatsAppService {

    void notifyCommerceNewOrder(Order order);

    void notifyCommerceCancelledOrder(Order order);

    void notifyClientOrderStatusChange(Order order);

    void notifyClientOrderConfirmation(Order order);
}