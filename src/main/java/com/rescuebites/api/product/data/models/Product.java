package com.rescuebites.api.product.data.models;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.shared.Image;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "productId")
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
    @PositiveOrZero(message = "El stock debe ser mayor a cero")
    private Integer stock;

    @NotNull(message = "El precio original es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio original debe ser mayor a 0")
    private BigDecimal originalPrice;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    private BigDecimal discountPercentage;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition", nullable = false)
    private ProductCondition condition;

    @ElementCollection(targetClass = PreferenceType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_preferences", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "preference_type")
    private List<PreferenceType> preferenceType = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CommerceTypeEnum commerceType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime updateAt;

    public BigDecimal getDiscountedPrice() {
        if (originalPrice == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = originalPrice.subtract(originalPrice.multiply(discountFactor));

        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }
}

