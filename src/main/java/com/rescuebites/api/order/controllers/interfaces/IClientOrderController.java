package com.rescuebites.api.order.controllers.interfaces;

import com.rescuebites.api.order.controllers.requests.CreateOrderRequest;
import com.rescuebites.api.order.controllers.responses.OrderResponse;
import com.rescuebites.api.order.controllers.responses.OrderSummaryForClientResponse;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/api/v1/clients/{clientId}/orders")
@Tag(name = "Orders - Client", description = "Gestión de pedidos del cliente")
public interface IClientOrderController {
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(
            summary = "Crear nuevo pedido",
            description = "Crea un pedido a partir del carrito del cliente. El carrito debe tener productos de un solo comercio."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío, productos de diferentes comercios, o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Cliente o comercio no encontrado")
    })
    OrderResponse createOrder(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Datos del pedido", required = true)
            @RequestBody @Valid CreateOrderRequest request
    );

    @GetMapping
    @Operation(
            summary = "Listar pedidos del cliente",
            description = "Retorna todos los pedidos del cliente ordenados por fecha (más reciente primero)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    Page<OrderSummaryForClientResponse> getClientOrders(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Obtener detalle de un pedido",
            description = "Retorna información detallada de un pedido específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    OrderResponse getOrderById(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "ID del pedido", required = true)
            @PathVariable UUID orderId
    );

    @PatchMapping("/{orderId}/cancel")
    @ResponseStatus(NO_CONTENT)
    @Operation(
            summary = "Cancelar pedido",
            description = "Cancela un pedido pendiente o confirmado. Restaura el stock de los productos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El pedido no puede ser cancelado en su estado actual"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    void cancelOrder(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "ID del pedido", required = true)
            @PathVariable UUID orderId,

            @Parameter(description = "Motivo de cancelación")
            @RequestParam(required = false) String reason
    );
}