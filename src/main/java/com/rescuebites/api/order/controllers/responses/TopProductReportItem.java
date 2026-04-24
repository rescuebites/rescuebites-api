package com.rescuebites.api.order.controllers.responses;

import java.math.BigDecimal;

public record TopProductReportItem(
        String name,
        Long unitsSold,
        BigDecimal revenue,
        BigDecimal percentage
) {}
