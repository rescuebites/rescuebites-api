package com.rescuebites.api.commerce.data.enums;

import java.util.EnumSet;

public enum ProductCheckType {

    RIPE(EnumSet.of(CommerceTypeEnum.GREENGROCERY)),
    ALMOST_RIPE(EnumSet.of(CommerceTypeEnum.GREENGROCERY)),
    FRUIT(EnumSet.of(CommerceTypeEnum.GREENGROCERY, CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK)),
    VEGETABLE(EnumSet.of(CommerceTypeEnum.GREENGROCERY, CommerceTypeEnum.SUPERMARKET)),
    FRESHLY_BAKED(EnumSet.of(CommerceTypeEnum.BAKERY)),
    READY_TO_SERVE(EnumSet.of(CommerceTypeEnum.RESTAURANT)),
    GOOD_CONDITION(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK)),
    DENTED(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK)),
    EXPIRING_SOON(EnumSet.of(CommerceTypeEnum.SUPERMARKET, CommerceTypeEnum.KIOSK)),
    OTHER(EnumSet.allOf(CommerceTypeEnum.class));

    private final EnumSet<CommerceTypeEnum> supportedCommerceTypes;

    ProductCheckType(EnumSet<CommerceTypeEnum> supportedCommerceTypes) {
        this.supportedCommerceTypes = supportedCommerceTypes;
    }

    public boolean isAllowedFor(CommerceTypeEnum commerceType) {
        return supportedCommerceTypes.contains(commerceType);
    }
}
