package com.rescuebites.api.order.services.implementations;

import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.order.controllers.responses.CommerceReportResponse;
import com.rescuebites.api.order.controllers.responses.TopProductReportItem;
import com.rescuebites.api.order.data.mappers.TopProductReportMapper;
import com.rescuebites.api.order.repositories.IOrderRepository;
import com.rescuebites.api.order.repositories.projections.TopProductProjection;
import com.rescuebites.api.order.services.interfaces.ICommerceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.order.utils.OrderConstants.TOP_PRODUCTS_LIMIT;

@Service
@RequiredArgsConstructor
public class CommerceReportServiceImpl implements ICommerceReportService {

    private final IOrderRepository orderRepository;
    private final ICommerceFacade commerceFacade;

    @Override
    @Transactional(readOnly = true)
    public CommerceReportResponse getSalesReport(UUID commerceId, LocalDate from, LocalDate to) {
        commerceFacade.findCommerceByIdOrThrowException(commerceId);
        commerceFacade.validateCommerceOwnership(commerceId);

        validateDateRange(from, to);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        BigDecimal totalSales = orderRepository.sumTotalSalesByCommerceAndPeriod(commerceId, fromDateTime, toDateTime);
        Long totalOrders = orderRepository.countCompletedOrdersByCommerceAndPeriod(commerceId, fromDateTime, toDateTime);
        Long totalProductsSold = orderRepository.sumTotalProductsSoldByCommerceAndPeriod(commerceId, fromDateTime, toDateTime);

        List<TopProductProjection> topRaw = orderRepository.findTopProductsByCommerceAndPeriod(
                commerceId, fromDateTime, toDateTime, PageRequest.of(0, TOP_PRODUCTS_LIMIT));

        List<TopProductReportItem> topProducts = TopProductReportMapper.toResponseList(topRaw, totalProductsSold);

        return new CommerceReportResponse(
                from,
                to,
                totalSales,
                totalOrders,
                totalProductsSold,
                topProducts
        );
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new ValidationException("Las fechas de inicio y fin son obligatorias");
        }
        if (from.isAfter(to)) {
            throw new ValidationException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }
}
