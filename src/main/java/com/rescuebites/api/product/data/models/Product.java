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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name = "products")
@Table(indexes = {
        @Index(name = "idx_product_active_commerce", columnList = "active, commerce_id"),
        @Index(name = "idx_product_active_name", columnList = "active, name"),
        @Index(name = "idx_product_active_commerce_type", columnList = "active, commerce_type")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @Column(name = "productId")
    @Builder.Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID productId = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private Commerce commerce;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

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

    @ElementCollection(targetClass = ProductCondition.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_conditions", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "condition_value")
    @BatchSize(size = 20)
    private Set<ProductCondition> conditions = new HashSet<>();

    @ElementCollection(targetClass = PreferenceType.class, fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_preferences", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "preference_type")
    @BatchSize(size = 20)
    private Set<PreferenceType> preferenceType = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private CommerceTypeEnum commerceType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @Builder.Default
    @BatchSize(size = 20)
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Formula("(original_price - (original_price * discount_percentage / 100))")
    private BigDecimal calculatedDiscountedPrice;

    private LocalDateTime updateAt;

    // Fecha y hora en que el sistema dio de baja automáticamente el producto al detectar que su fecha de vencimiento ya había pasado.
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    @Column(name = "normalized_description", nullable = false)
    private String normalizedDescription;

    public BigDecimal getDiscountedPrice() {
        if (originalPrice == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = originalPrice.subtract(originalPrice.multiply(discountFactor));

        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }
}
