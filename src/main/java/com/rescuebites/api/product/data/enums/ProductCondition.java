package com.rescuebites.api.product.data.enums;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import lombok.Getter;

import java.util.EnumSet;

public enum ProductCondition {

    // Estados generales
    EXCELLENT(EnumSet.allOf(CommerceTypeEnum.class), "Excelente estado"),
    GOOD(EnumSet.allOf(CommerceTypeEnum.class), "Buen estado"),

    // Específicos de verdulería
    RIPE(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Maduro"),
    ALMOST_RIPE(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Casi maduro"),
    OVERRIPE(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Pasado de madurez"),

    // Específicos de panadería
    FRESHLY_BAKED(EnumSet.of(CommerceTypeEnum.BAKERY), "Recién horneado"),
    SAME_DAY(EnumSet.of(CommerceTypeEnum.BAKERY), "Del día"),
    PREVIOUS_DAY(EnumSet.of(CommerceTypeEnum.BAKERY), "Día anterior"),

    // Específicos de restaurante
    READY_TO_SERVE(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Listo para servir"),
    NEEDS_REHEATING(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Requiere recalentar"),

    // Específicos de supermercado/kiosco
    DENTED_PACKAGING(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK), "Envase abollado"),
    NEAR_EXPIRY(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK), "Próximo a vencer"),
    EXPIRED_TODAY(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK), "Vence hoy"),
    DAMAGED_LABEL(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK), "Etiqueta dañada"),

    OTHER(EnumSet.allOf(CommerceTypeEnum.class), "Otro");

    private final EnumSet<CommerceTypeEnum> supportedCommerceTypes;
    @Getter
    private final String displayName;

    ProductCondition(EnumSet<CommerceTypeEnum> supportedCommerceTypes, String displayName) {
        this.supportedCommerceTypes = supportedCommerceTypes;
        this.displayName = displayName;
    }

    public boolean isAllowedFor(CommerceTypeEnum commerceType) {
        return supportedCommerceTypes.contains(commerceType);
    }

    public static EnumSet<ProductCondition> getAllowedFor(CommerceTypeEnum commerceType) {
        EnumSet<ProductCondition> allowed = EnumSet.noneOf(ProductCondition.class);
        for (ProductCondition condition : ProductCondition.values()) {
            if (condition.isAllowedFor(commerceType)) {
                allowed.add(condition);
            }
        }
        return allowed;
    }
}