package com.rescuebites.api.order.services.interfaces;

import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForCommerceResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICommerceOrderService {

    Page<OrderSummaryForCommerceResponse> getCommerceOrders(UUID commerceId, Pageable pageable);

    Page<OrderSummaryForCommerceResponse> getCommerceOrdersByStatus(UUID commerceId, OrderStatus status, Pageable pageable);

    OrderResponse getCommerceOrderById(UUID commerceId, UUID orderId);

    void updateOrderStatus(UUID commerceId, UUID orderId, OrderStatus newStatus, String reason);
}