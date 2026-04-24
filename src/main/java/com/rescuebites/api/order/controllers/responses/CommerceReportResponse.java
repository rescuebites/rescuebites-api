package com.rescuebites.api.order.controllers.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CommerceReportResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal totalSales,
        Long totalOrders,
        Long totalProductsSold,
        List<TopProductReportItem> topProducts
) {}

