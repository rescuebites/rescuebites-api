package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchProductResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/v1/commerces/{commerceId}/search")
@Tag(name = "Commerce Search", description = "Endpoints de búsqueda para el home del comercio autenticado")
public interface ICommerceSearchController {

    @GetMapping("/suggestions")
    @ResponseStatus(OK)
    @Operation(
            summary = "Obtener sugerencias de búsqueda (autocomplete)",
            description = "Retorna sugerencias de los productos propios del comercio que coinciden con el término ingresado. " +
                    "Jerarquía: nombre de producto > descripción de producto."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query inválido"),
            @ApiResponse(responseCode = "401", description = "Comercio no autenticado"),
            @ApiResponse(responseCode = "403", description = "No tenés permiso para acceder a este comercio"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    List<SearchSuggestion> getSuggestions(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Término de búsqueda", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query
    );

    @GetMapping
    @ResponseStatus(OK)
    @Operation(
            summary = "Buscar productos del comercio",
            description = "Retorna los productos del comercio autenticado que coinciden con el término ingresado. " +
                    "Jerarquía: primero los que coinciden por nombre, luego los que coinciden por descripción. " +
                    "También incluye coincidencias parciales por cada palabra del término ingresado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query inválido"),
            @ApiResponse(responseCode = "401", description = "Comercio no autenticado"),
            @ApiResponse(responseCode = "403", description = "No tenés permiso para acceder a este comercio"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    Page<SearchProductResponse> search(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Término de búsqueda", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query,

            @Parameter(description = "Parámetros de paginación (page, size, sort)")
            Pageable pageable
    );
}
