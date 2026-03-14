package com.rescuebites.api.order.data.models;

import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.order.data.enums.OrderStatus;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @Column(name = "order_id")
    @Builder.Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID orderId = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private Commerce commerce;

    @Column(nullable = false, unique = true)
    private String orderNumber; // Ej: "ORD-20250129-001234"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal; // Suma de items

    @NotNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total; // Total final (por ahora igual a subtotal)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime confirmedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    @Column(length = 500)
    private String cancellationReason;

    @Column(name = "scheduled_pickup_time")
    private LocalTime scheduledPickupTime;

    @Column(length = 500)
    private String notes;
}