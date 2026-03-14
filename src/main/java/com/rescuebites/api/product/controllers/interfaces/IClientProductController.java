package com.rescuebites.api.product.controllers.interfaces;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/api/v1/clients/{clientId}/products")
@Tag(name = "Client Products", description = "Endpoints de productos para clientes autenticados, filtrados por preferencias dietéticas")
public interface IClientProductController {

    @GetMapping("/preferences")
    @Operation(
            summary = "Listar productos que coinciden con las preferencias dietéticas del cliente",
            description = "Retorna una lista paginada de productos activos que cumplen con las preferencias dietéticas " +
                    "(celíaco, vegano, sin gluten, etc) del cliente autenticado. La localidad utilizada para filtrar " +
                    "es la localidad registrada en el perfil del cliente (no se acepta sobreescritura por query param)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "Cliente no autenticado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    Page<ProductResponse> getProductsMatchingClientPreferences(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/commerce/{commerceId}")
    @Operation(
            summary = "Listar productos activos de un comercio filtrados por preferencias del cliente",
            description = "Retorna una lista paginada de productos activos de un comercio específico que coinciden con las preferencias del cliente. " +
                    "La localidad utilizada para filtrar es la localidad registrada en el perfil del cliente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente o comercio no encontrado")
    })
    Page<ProductResponse> getActiveProductsByCommerce(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/ordered-by-price")
    @Operation(
            summary = "Listar productos activos filtrados por preferencias y ordenados por precio",
            description = "Retorna una lista paginada de productos activos que coinciden con las preferencias del cliente, ordenados por precio con descuento. " +
                    "La localidad utilizada para filtrar es la localidad registrada en el perfil del cliente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    Page<ProductResponse> getAllActiveProductsOrderedByPrice(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/type/{commerceType}/ordered-by-price")
    @Operation(
            summary = "Listar productos por tipo de comercio filtrados por preferencias y ordenados por precio",
            description = "Retorna una lista paginada de productos activos filtrados por tipo de comercio y preferencias del cliente, ordenados por precio. " +
                    "La localidad utilizada para filtrar es la localidad registrada en el perfil del cliente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tipo de comercio inválido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID clientId,

            @Parameter(description = "Tipo de comercio", required = true)
            @PathVariable CommerceTypeEnum commerceType,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );
}
