package com.rescuebites.api.product.data.enums;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import lombok.Getter;

import java.util.EnumSet;

public enum ProductCategory {

    // VERDULERÍA
    FRUIT(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Fruta"),
    VEGETABLE(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Verdura"),
    HERBS(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Hierbas"),
    TUBER(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Tubérculo"),
    SEEDLING(EnumSet.of(CommerceTypeEnum.GREENGROCERY), "Plantines"),

    // PANADERÍA
    BREAD(EnumSet.of(CommerceTypeEnum.BAKERY), "Pan"),
    PASTRIES(EnumSet.of(CommerceTypeEnum.BAKERY), "Facturas"),
    CAKES(EnumSet.of(CommerceTypeEnum.BAKERY), "Tortas"),
    COOKIES(EnumSet.of(CommerceTypeEnum.BAKERY), "Galletas"),
    DOUGH(EnumSet.of(CommerceTypeEnum.BAKERY), "Masas"),
    DESSERTS_BAKERY(EnumSet.of(CommerceTypeEnum.BAKERY), "Postres"),

    // RESTAURANTE
    APPETIZERS(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Entradas"),
    MAIN_COURSES(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Platos Principales"),
    DESSERTS_RESTAURANT(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Postres"),
    BEVERAGES_RESTAURANT(EnumSet.of(CommerceTypeEnum.RESTAURANT), "Bebidas"),

    // KIOSCO/SUPERMERCADO
    CLEANING(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Limpieza"),
    BEVERAGES(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Bebidas"),
    GROCERIES(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Comestibles"),
    FRESH_PRODUCTS(EnumSet.of(CommerceTypeEnum.SUPERMARKET), "Frescos"),
    FROZEN(EnumSet.of(CommerceTypeEnum.SUPERMARKET), "Congelados"),
    PERSONAL_HYGIENE(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Higiene Personal"),
    CANDY(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Golosinas"),
    SNACKS(EnumSet.of(CommerceTypeEnum.KIOSK, CommerceTypeEnum.SUPERMARKET), "Snacks"),
    CIGARETTES(EnumSet.of(CommerceTypeEnum.KIOSK), "Cigarrillos"),
    MAGAZINES(EnumSet.of(CommerceTypeEnum.KIOSK), "Revistas"),

    // CATEGORÍAS CROSS (productos de otras categorías vendidos en super/kiosco)
    GREENGROCERY_SECTION(EnumSet.of(CommerceTypeEnum.SUPERMARKET), "Verdulería"),
    BAKERY_SECTION(EnumSet.of(CommerceTypeEnum.SUPERMARKET), "Panadería"),

    // GENÉRICO
    OTHER(EnumSet.allOf(CommerceTypeEnum.class), "Otro");

    private final EnumSet<CommerceTypeEnum> supportedCommerceTypes;
    @Getter
    private final String displayName;

    ProductCategory(EnumSet<CommerceTypeEnum> supportedCommerceTypes, String displayName) {
        this.supportedCommerceTypes = supportedCommerceTypes;
        this.displayName = displayName;
    }

    public boolean isAllowedFor(CommerceTypeEnum commerceType) {
        return supportedCommerceTypes.contains(commerceType);
    }

    public static EnumSet<ProductCategory> getAllowedFor(CommerceTypeEnum commerceType) {
        EnumSet<ProductCategory> allowed = EnumSet.noneOf(ProductCategory.class);
        for (ProductCategory category : ProductCategory.values()) {
            if (category.isAllowedFor(commerceType)) {
                allowed.add(category);
            }
        }
        return allowed;
    }
}