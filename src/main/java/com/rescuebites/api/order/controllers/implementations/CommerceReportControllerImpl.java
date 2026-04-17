package com.rescuebites.api.order.controllers.implementations;

import com.rescuebites.api.order.controllers.interfaces.ICommerceReportController;
import com.rescuebites.api.order.controllers.responses.CommerceReportResponse;
import com.rescuebites.api.order.services.interfaces.ICommerceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommerceReportControllerImpl implements ICommerceReportController {

    private final ICommerceReportService commerceReportService;

    @Override
    public CommerceReportResponse getSalesReport(UUID commerceId, LocalDate from, LocalDate to) {
        return commerceReportService.getSalesReport(commerceId, from, to);
    }
}
