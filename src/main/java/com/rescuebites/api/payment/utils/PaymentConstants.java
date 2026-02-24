package com.rescuebites.api.payment.utils;

import com.rescuebites.api.order.data.enums.PaymentMethod;

import java.util.Arrays;
import java.util.List;

public class PaymentConstants {

    public static final List<PaymentMethod> ACCEPTED_PAYMENT_METHODS = Arrays.asList(
            PaymentMethod.MERCADO_PAGO,
            PaymentMethod.CASH
    );

    public static final String CURRENCY_ID = "ARS";
    public static final String AUTO_RETURN_VALUE = "approved";
    public static final String STATEMENT_DESCRIPTOR = "RescueBites";
    public static final String PAYMENT_TYPE = "payment";
}
