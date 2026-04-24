package com.rescuebites.api.payment.controllers.interfaces;

import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.controllers.responses.PaymentStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Gestión de pagos con Mercado Pago")
public interface IPaymentController {

    @PostMapping("/orders/{orderId}/create-preference")
    @Operation(
            summary = "Crear preferencia de pago",
            description = "Genera un link de pago de Mercado Pago para un pedido específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link de pago generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "El pedido ya fue pagado o no está en estado válido")
    })
    PaymentLinkResponse createPaymentPreference(
            @Parameter(description = "ID del pedido", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "URL base del frontend para las redirecciones de pago", required = false)
            @RequestParam(required = false) String frontendBaseUrl
    );

    @PostMapping("/webhook")
    @Operation(
            summary = "Webhook de Mercado Pago",
            description = "Endpoint para recibir notificaciones de cambios de estado de pagos desde Mercado Pago"
    )
    void handleMercadoPagoWebhook(
            @RequestBody String notification,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId
    );

    @PostMapping("/orders/{orderId}/confirm")
    @Operation(
            summary = "Confirm order (client callback)",
            description = "Endpoint público que puede invocar el frontend al aterrizar en la página de éxito para asegurar que la orden quede confirmada (fallback cuando el webhook no llega)."
    )
    ResponseEntity<Void> confirmOrder(
            @Parameter(description = "ID del pedido")
            @PathVariable UUID orderId
    );

    @GetMapping("/success")
    @Operation(
            summary = "Página de éxito de pago",
            description = "Redirección después de un pago exitoso"
    )
    ResponseEntity<Void> paymentSuccess(
            @Parameter(description = "ID del pedido")
            @RequestParam UUID orderId
    );

    @GetMapping("/failure")
    @Operation(
            summary = "Página de fallo de pago",
            description = "Redirección después de un pago fallido"
    )
    ResponseEntity<Void> paymentFailure(
            @Parameter(description = "ID del pedido")
            @RequestParam UUID orderId
    );

    @GetMapping("/pending")
    @Operation(
            summary = "Página de pago pendiente",
            description = "Redirección cuando el pago está pendiente"
    )
    ResponseEntity<Void> paymentPending(
            @Parameter(description = "ID del pedido")
            @RequestParam UUID orderId
    );
}