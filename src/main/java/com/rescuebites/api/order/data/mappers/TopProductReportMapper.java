package com.rescuebites.api.order.data.mappers;

import com.rescuebites.api.order.controllers.responses.TopProductReportItem;
import com.rescuebites.api.order.repositories.projections.TopProductProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class TopProductReportMapper {

    private TopProductReportMapper() {}

    public static List<TopProductReportItem> toResponseList(List<TopProductProjection> projections, Long totalUnitsSold) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }

        long topUnitsSum = projections.stream().mapToLong(TopProductProjection::getUnits).sum();
        BigDecimal total = BigDecimal.valueOf(topUnitsSum > 0 ? topUnitsSum : 1L);

        return projections.stream()
                .map(p -> toResponse(p, total))
                .toList();
    }

    private static TopProductReportItem toResponse(TopProductProjection projection, BigDecimal totalUnitsSold) {
        BigDecimal units = BigDecimal.valueOf(projection.getUnits());
        BigDecimal revenue = projection.getRevenue() != null ? projection.getRevenue() : BigDecimal.ZERO;

        BigDecimal percentage = units
                .multiply(BigDecimal.valueOf(100))
                .divide(totalUnitsSold, 2, RoundingMode.HALF_UP);

        return new TopProductReportItem(
                projection.getName(),
                projection.getUnits(),
                revenue,
                percentage
        );
    }
}
