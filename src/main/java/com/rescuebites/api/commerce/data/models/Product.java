package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.commerce.data.enums.ProductCheckType;
import com.rescuebites.api.shared.Image;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name = "product_id")
    @Builder.Default
    private UUID productId = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private Commerce commerce;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser mayor a cero")
    private Integer stock;

    @NotNull(message = "El precio original es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio original debe ser mayor a 0")
    private BigDecimal originalPrice;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    private BigDecimal discountPercentage;

    private LocalDate expirationDate;

    @ElementCollection(targetClass = ProductCheckType.class)
    @CollectionTable(
            name = "product_checks",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<ProductCheckType> checks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    public BigDecimal getDiscountedPrice() {
        if (originalPrice == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100));
        return originalPrice.subtract(originalPrice.multiply(discountFactor));
    }
}
