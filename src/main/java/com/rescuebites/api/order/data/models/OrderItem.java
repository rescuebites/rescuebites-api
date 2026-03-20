package com.rescuebites.api.order.data.models;

import com.rescuebites.api.product.data.models.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OrderItem {

    @Id
    @Column(name = "order_item_id")
    @Builder.Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID orderItemId = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    private String productName;

    @NotNull
    private String productDescription;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @NotNull
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice; // Precio con descuento aplicado

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal; // unitPrice * quantity

    public void calculateSubtotal() {
        this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}