package com.rescuebites.api.shared.controllers.interfaces;

import com.rescuebites.api.shared.controllers.responses.SearchResultResponse;
import com.rescuebites.api.shared.controllers.responses.SearchSuggestion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/v1/clients/{clientId}/search")
@Tag(name = "Client Search", description = "Endpoints de búsqueda protegidos para clientes autenticados")
public interface IClientSearchController {

    @GetMapping("/suggestions")
    @ResponseStatus(OK)
    @Operation(
            summary = "Obtener sugerencias de búsqueda (autocomplete)",
            description = "Retorna sugerencias de comercios y productos filtrados por las preferencias del cliente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query inválido"),
            @ApiResponse(responseCode = "401", description = "Cliente no autenticado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    List<SearchSuggestion> getSuggestions(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Término de búsqueda", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query
    );

    @GetMapping
    @ResponseStatus(OK)
    @Operation(
            summary = "Buscar (resultado compuesto) según preferencias del cliente",
            description = "Retorna productos que coincidan con el término de búsqueda y las preferencias dietéticas del cliente. " +
                    "Si el término coincide exactamente con el nombre de un comercio registrado, incluye el detalle del comercio. " +
                    "Jerarquía de productos: 1) productos del comercio coincidente (si existe), 2) por nombre, 3) por descripción."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos exitosamente"),
            @ApiResponse(responseCode = "400", description = "Query inválido"),
            @ApiResponse(responseCode = "401", description = "Cliente no autenticado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    SearchResultResponse searchWithPreferences(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Término de búsqueda", required = true)
            @RequestParam("q") @NotBlank(message = "El término de búsqueda es requerido") String query,

            Pageable pageable
    );
}
