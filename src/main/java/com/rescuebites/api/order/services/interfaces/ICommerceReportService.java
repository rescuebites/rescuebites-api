package com.rescuebites.api.order.services.interfaces;

import com.rescuebites.api.order.controllers.responses.CommerceReportResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ICommerceReportService {

    CommerceReportResponse getSalesReport(UUID commerceId, LocalDate from, LocalDate to);
}
