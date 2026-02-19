package com.rescuebites.api.order.controllers.implementations;

import com.rescuebites.api.order.controllers.interfaces.IClientOrderController;
import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.services.interfaces.IClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientOrderControllerImpl implements IClientOrderController {

    private final IClientOrderService clientOrderService;

    @Override
    public OrderResponse createOrder(UUID clientId, CreateOrderRequest request) {
        return clientOrderService.createOrder(clientId, request);
    }

    @Override
    public Page<OrderResponse> getClientOrders(UUID clientId, Pageable pageable) {
        return clientOrderService.getClientOrders(clientId, pageable);
    }

    @Override
    public OrderResponse getOrderById(UUID clientId, UUID orderId) {
        return clientOrderService.getOrderById(clientId, orderId);
    }

    @Override
    public void cancelOrder(UUID clientId, UUID orderId, String reason) {
        clientOrderService.cancelOrder(clientId, orderId, reason);
    }
}