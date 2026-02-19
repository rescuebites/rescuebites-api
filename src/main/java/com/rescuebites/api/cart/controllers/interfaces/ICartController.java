package com.rescuebites.api.cart.controllers.interfaces;

import com.rescuebites.api.cart.controllers.requests.AddToCartRequest;
import com.rescuebites.api.cart.controllers.requests.UpdateCartItemRequest;
import com.rescuebites.api.cart.controllers.requests.UpdatePaymentMethodRequest;
import com.rescuebites.api.cart.controllers.responses.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/api/v1/clients/{clientId}/cart")
@Tag(name = "Cart", description = "Gestión del carrito de compras")
public interface ICartController {

    @GetMapping
    @Operation(
            summary = "Obtener carrito del cliente",
            description = "Retorna el carrito actual del cliente con todos sus items"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    CartResponse getCart(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId
    );

    @PostMapping("/items")
    @ResponseStatus(CREATED)
    @Operation(
            summary = "Agregar producto al carrito",
            description = "Agrega un producto al carrito o actualiza la cantidad si ya existe"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado")
    })
    CartResponse addToCart(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Datos del producto a agregar", required = true)
            @RequestBody @Valid AddToCartRequest request
    );

    @PatchMapping("/items/{cartItemId}")
    @Operation(
            summary = "Actualizar cantidad de un item del carrito",
            description = "Actualiza la cantidad de un producto en el carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    CartResponse updateCartItem(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "ID del item del carrito", required = true)
            @PathVariable UUID cartItemId,

            @Parameter(description = "Nueva cantidad", required = true)
            @RequestBody @Valid UpdateCartItemRequest request
    );

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
            summary = "Eliminar item del carrito",
            description = "Elimina un producto del carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    void removeFromCart(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "ID del item del carrito", required = true)
            @PathVariable UUID cartItemId
    );

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @Operation(
            summary = "Vaciar carrito",
            description = "Elimina todos los items del carrito"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    void clearCart(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId
    );

    @PatchMapping("/payment-method")
    @Operation(
            summary = "Actualizar método de pago del carrito",
            description = "Actualiza el método de pago seleccionado para el carrito (MERCADO_PAGO o CASH)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Método de pago actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Método de pago inválido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    CartResponse updatePaymentMethod(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Datos del método de pago a actualizar", required = true)
            @RequestBody @Valid UpdatePaymentMethodRequest request
    );
}