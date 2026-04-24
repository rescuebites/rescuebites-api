package com.rescuebites.api.order.repositories.projections;

import java.math.BigDecimal;

public interface TopProductProjection {
    String getName();
    Long getUnits();
    BigDecimal getRevenue();
}
