package com.rescuebites.api.order.services.implementations;

import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.notifications.Interfaces.INotificationService;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForCommerceResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.mappers.OrderMapper;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.order.facades.interfaces.IOrderValidationFacade;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.order.services.interfaces.ICommerceOrderService;
import com.rescuebites.api.order.utils.OrderStatusValidator;
import com.rescuebites.api.shared.services.interfaces.IWhatsAppService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommerceOrderServiceImpl implements ICommerceOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderValidationFacade orderValidationFacade;
    private final IWhatsAppService whatsAppService;
    private final ICommerceFacade commerceFacade;
    private final INotificationService notificationService;

    @Override
    @Transactional
    public Page<OrderSummaryForCommerceResponse> getCommerceOrders(UUID commerceId, Pageable pageable) {
        commerceFacade.validateCommerceOwnership(commerceId);
        Page<Order> orders = orderRepository.findByCommerceId(commerceId, pageable);
        return orders.map(OrderMapper::toOrderSummaryForCommerce);
    }

    @Override
    @Transactional
    public Page<OrderSummaryForCommerceResponse> getCommerceOrdersByStatus(UUID commerceId, OrderStatus status, Pageable pageable) {
        commerceFacade.validateCommerceOwnership(commerceId);
        Page<Order> orders = orderRepository.findByCommerceIdAndStatus(commerceId, status, pageable);
        return orders.map(OrderMapper::toOrderSummaryForCommerce);
    }

    @Override
    @Transactional
    public OrderResponse getCommerceOrderById(UUID commerceId, UUID orderId) {
        commerceFacade.validateCommerceOwnership(commerceId);
        Order order = orderValidationFacade.findOrderByIdAndCommerceId(orderId, commerceId);

        return OrderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(UUID commerceId, UUID orderId, OrderStatus newStatus, String reason) {
        commerceFacade.validateCommerceOwnership(commerceId);
        Order order = orderValidationFacade.findOrderByIdAndCommerceId(orderId, commerceId);

        OrderStatusValidator.validateStatusTransition(order.getStatus(), newStatus);

        if (newStatus == OrderStatus.CANCELLED && (reason == null || reason.isBlank())) {
            throw new ValidationException("El motivo de cancelación es obligatorio");
        }

        order.setStatus(newStatus);
        applyStatusTimestamp(order, newStatus, reason);

        orderRepository.save(order);

        // 🔥 ESTA ES LA CLAVE
        notificationService.notifyOrderStatusChange(order, newStatus);
        whatsAppService.notifyClientOrderStatusChange(order);
    }

    private void applyStatusTimestamp(Order order, OrderStatus newStatus, String reason) {
        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(LocalDateTime.now());
            case COMPLETED -> order.setCompletedAt(LocalDateTime.now());
            case CANCELLED -> {
                order.setCancelledAt(LocalDateTime.now());
                order.setCancellationReason(reason);
            }
            default -> {
            } // PENDING, PREPARING, READY no tienen timestamp específico
        }
    }
}