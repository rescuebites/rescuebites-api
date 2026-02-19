package com.rescuebites.api.order.services.interfaces;

import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IClientOrderService {

    OrderResponse createOrder(UUID clientId, CreateOrderRequest request);

    OrderResponse getOrderById(UUID clientId, UUID orderId);

    Page<OrderResponse> getClientOrders(UUID clientId, Pageable pageable);

    void cancelOrder(UUID clientId, UUID orderId, String reason);
}
