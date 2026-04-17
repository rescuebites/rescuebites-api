package com.rescuebites.api.order.controllers.interfaces;

import com.rescuebites.api.order.controllers.responses.CommerceReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/api/v1/commerces/{commerceId}/reports")
@Tag(name = "Reports - Commerce", description = "Reportes de ventas del comercio")
public interface ICommerceReportController {

    @GetMapping("/sales")
    @Operation(
            summary = "Reporte de ventas del comercio",
            description = "Genera un reporte de ventas del comercio para el rango de fechas indicado. " +
                    "Solo considera pedidos con estado COMPLETED. " +
                    "Requiere autenticación — solo el dueño del comercio puede consultar su propio reporte."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "403", description = "No tenés permiso para consultar este reporte"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    CommerceReportResponse getSalesReport(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Fecha de inicio del período (ISO 8601: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "Fecha de fin del período (ISO 8601: yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    );
}
