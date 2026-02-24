package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/api/v1/commerces")
public interface ICommerceController {

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