package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/public/commerces")
@Tag(name = "Public Commerces", description = "Endpoints públicos de comercios para clientes")
public interface IPublicCommerceController {

    @GetMapping("/type/{commerceType}")
    @Operation(
            summary = "Listar comercios por tipo",
            description = "Retorna una lista paginada de comercios activos filtrados por tipo de comercio"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comercios obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tipo de comercio inválido")
    })
    Page<CommercePublicResponse> getCommercesByType(
            @Parameter(description = "Tipo de comercio", required = true)
            @PathVariable CommerceTypeEnum commerceType,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );
}