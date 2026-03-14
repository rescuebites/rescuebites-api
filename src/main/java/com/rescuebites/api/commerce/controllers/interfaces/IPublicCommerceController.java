package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/public/commerces")
@Tag(name = "Public Commerces", description = "Endpoints públicos de comercios para clientes")
public interface IPublicCommerceController {

    @GetMapping
    @Operation(
            summary = "Listar todos los comercios",
            description = "Retorna una lista paginada de todos los comercios activos de la localidad especificada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comercios obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Localidad no especificada")
    })
    Page<CommercePublicResponse> getAllCommerces(
            @Parameter(description = "Localidad para filtrar comercios", required = true)
            @RequestParam String locality,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/{commerceId}")
    @Operation(
            summary = "Obtener detalle de un comercio",
            description = "Retorna la información completa de un comercio específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comercio obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    CommerceResponse getCommerceById(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId
    );

    @GetMapping("/type/{commerceType}")
    @Operation(
            summary = "Listar comercios por tipo",
            description = "Retorna una lista paginada de comercios activos filtrados por tipo de comercio y localidad."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comercios obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tipo de comercio o localidad inválidos")
    })
    Page<CommercePublicResponse> getCommercesByType(
            @Parameter(description = "Tipo de comercio", required = true)
            @PathVariable CommerceTypeEnum commerceType,

            @Parameter(description = "Localidad para filtrar comercios", required = true)
            @RequestParam String locality,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );
}