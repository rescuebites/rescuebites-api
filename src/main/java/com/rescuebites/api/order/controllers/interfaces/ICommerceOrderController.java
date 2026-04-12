package com.rescuebites.api.order.controllers.interfaces;

import com.rescuebites.api.order.controllers.requests.UpdateOrderStatusRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForCommerceResponse;
import com.rescuebites.api.order.data.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/api/v1/commerces/{commerceId}/orders")
@Tag(name = "Orders - Commerce", description = "Gestión de pedidos del comercio")
public interface ICommerceOrderController {

    @GetMapping
    @Operation(
            summary = "Listar pedidos del comercio",
            description = "Retorna todos los pedidos del comercio ordenados por fecha (más reciente primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    Page<OrderSummaryForCommerceResponse> getCommerceOrders(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Listar pedidos por estado",
            description = "Retorna pedidos del comercio filtrados por estado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    Page<OrderSummaryForCommerceResponse> getCommerceOrdersByStatus(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Estado del pedido", required = true)
            @PathVariable OrderStatus status,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Obtener detalle de un pedido",
            description = "Retorna información detallada de un pedido específico del comercio"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    OrderResponse getCommerceOrderById(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "ID del pedido", required = true)
            @PathVariable UUID orderId
    );

    @PatchMapping("/{orderId}/status")
    @ResponseStatus(NO_CONTENT)
    @Operation(
            summary = "Actualizar estado del pedido",
            description = "Cambia el estado de un pedido (ej: de PENDING a CONFIRMED, de CONFIRMED a PREPARING, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    void updateOrderStatus(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "ID del pedido", required = true)
            @PathVariable UUID orderId,

            @Parameter(description = "Nuevo estado", required = true)
            @RequestBody @Valid UpdateOrderStatusRequest request
    );
}