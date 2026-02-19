package com.rescuebites.api.order.controllers.implementations;

import com.rescuebites.api.order.controllers.interfaces.ICommerceOrderController;
import com.rescuebites.api.order.controllers.requests.UpdateOrderStatusRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.services.interfaces.ICommerceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommerceOrderControllerImpl implements ICommerceOrderController {

    private final ICommerceOrderService commerceOrderService;

    @Override
    public Page<OrderResponse> getCommerceOrders(UUID commerceId, Pageable pageable) {
        return commerceOrderService.getCommerceOrders(commerceId, pageable);
    }

    @Override
    public Page<OrderResponse> getCommerceOrdersByStatus(UUID commerceId, OrderStatus status, Pageable pageable) {
        return commerceOrderService.getCommerceOrdersByStatus(commerceId, status, pageable);
    }

    @Override
    public OrderResponse getCommerceOrderById(UUID commerceId, UUID orderId) {
        return commerceOrderService.getCommerceOrderById(commerceId, orderId);
    }

    @Override
    public void updateOrderStatus(UUID commerceId, UUID orderId, UpdateOrderStatusRequest request) {
        commerceOrderService.updateOrderStatus(commerceId, orderId, request.newStatus(), request.reason());
    }
}