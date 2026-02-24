package com.rescuebites.api.shared.controllers.interfaces;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/search")
@Tag(name = "Public Search", description = "Endpoints públicos de búsqueda")
public interface IPublicSearchController {

    @GetMapping("/suggestions")
    @ResponseStatus(OK)
    @Operation(
            summary = "Obtener sugerencias de búsqueda (Autocomplete)",
            description = "Retorna sugerencias de productos y comercios mientras el cliente escribe. " +
                    "Jerarquía: nombre de comercio > nombre de producto > descripción de producto. " +
                    "Se muestra máximo 5 productos y 5 comercios ordenados alfabéticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query inválido")
    })
    List<SearchSuggestion> getSuggestions(
            @Parameter(description = "Término de búsqueda para autocomplete", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query
    );

    @GetMapping
    @ResponseStatus(OK)
    @Operation(
            summary = "Buscar productos con paginación",
            description = "Retorna los productos que coincidan con el término de búsqueda. " +
                    "Busca en: nombre de comercio, nombre de producto y descripción de producto. " +
                    "Jerarquía de resultados: 1) Productos del comercio encontrado, 2) Productos por nombre, 3) Productos por descripción."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query o paginación inválida")
    })
    Page<SearchProductResponse> search(
            @Parameter(description = "Término de búsqueda", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query,

            @Parameter(description = "Objeto de paginación (page=0&size=20)", required = false)
            Pageable pageable
    );
}
