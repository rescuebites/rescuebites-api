package com.rescuebites.api.cart.data.models;

import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.order.data.enums.PaymentMethod.CASH;

@Entity(name = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @Column(name = "cart_id")
    @Builder.Default
    private UUID cartId = UUID.randomUUID();

    @OneToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_payment_method")
    @Builder.Default
    private PaymentMethod selectedPaymentMethod = CASH;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void clear() {
        this.items.clear();
        this.updatedAt = LocalDateTime.now();
    }
}