package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.BusinessHoursValidationResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceIdentityCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/api/v1/commerces")
public interface ICommerceController {

    @GetMapping("/identity/availability")
    @ResponseStatus(OK)
    @Operation(
            summary = "Verificar disponibilidad de identidad del comercio",
            description = "Verifica si ya existe un comercio activo con el mismo nombre, dirección y localidad. " +
                    "Endpoint público — no requiere autenticación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación de identidad"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    CommerceIdentityCheckResponse checkIdentityAvailability(
            @Parameter(description = "Nombre del comercio", required = true)
            @RequestParam String name,

            @Parameter(description = "Dirección del comercio", required = true)
            @RequestParam String address,

            @Parameter(description = "Localidad del comercio", required = true)
            @RequestParam String locality,

            @Parameter(description = "ID del comercio a excluir (para el caso de actualización)")
            @RequestParam(required = false) UUID excludeCommerceId
    );

    @PostMapping("/validate-business-hours")
    @ResponseStatus(OK)
    @Operation(
            summary = "Validar horarios de atención",
            description = "Verifica que los horarios de atención cumplan todas las reglas de negocio " +
                    "(apertura < cierre, turno tarde posterior al turno mañana, todos los días presentes, etc.). " +
                    "Endpoint público — no requiere autenticación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación de horarios"),
            @ApiResponse(responseCode = "400", description = "Body inválido")
    })
    BusinessHoursValidationResponse validateBusinessHours(
            @Parameter(description = "Lista de horarios de atención a validar", required = true)
            @org.springframework.web.bind.annotation.RequestBody List<@Valid BusinessHoursRequest> businessHours
    );

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    void createCommerce(@RequestPart("commerce") @Valid CreateCommerceRequest createCommerceRequest,
                        @RequestPart("images") MultipartFile[] images);

    @PatchMapping(value = "/{commerceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(OK)
    void updateCommerce(@PathVariable("commerceId") UUID commerceId,
                        @RequestPart("commerce") @Valid UpdateCommerceRequest updateCommerceRequest,
                        @RequestPart(value = "images", required = false) MultipartFile[] images);

    @DeleteMapping("/{commerceId}")
    @ResponseStatus(NO_CONTENT)
    void deleteCommerce(@PathVariable UUID commerceId);

    @PatchMapping("/{commerceId}/credentials")
    @ResponseStatus(OK)
    @Operation(
            summary = "Actualizar credenciales de Mercado Pago",
            description = "Permite al dueño del comercio actualizar su access token y webhook secret de Mercado Pago"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credenciales actualizadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para actualizar este comercio"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    void updateCredentials(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Nuevas credenciales de Mercado Pago", required = true)
            @RequestBody @Valid UpdateCommerceCredentialsRequest request
    );
}