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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/public/products")
@Tag(name = "Public Products", description = "Endpoints públicos de productos para clientes")
public interface IPublicProductController {

    @GetMapping
    @Operation(
            summary = "Listar todos los productos activos",
            description = "Retorna una lista paginada de todos los productos activos disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    Page<ProductResponse> getAllActiveProducts(
            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/{productId}")
    @Operation(
            summary = "Obtener detalle de un producto",
            description = "Retorna la información detallada de un producto activo específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado o inactivo")
    })
    ProductResponse getProductById(
            @Parameter(description = "ID del producto", required = true)
            @PathVariable UUID productId
    );

    @GetMapping("/commerce/{commerceId}")
    @Operation(
            summary = "Listar productos activos de un comercio",
            description = "Retorna una lista paginada de todos los productos activos de un comercio específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    Page<ProductResponse> getActiveProductsByCommerce(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/ordered-by-price")
    @Operation(
            summary = "Listar productos activos ordenados por precio al inicio del home",
            description = "Retorna una lista paginada al inicio del home de productos activos ordenados por precio con descuento de mayor a menor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    Page<ProductResponse> getAllActiveProductsOrderedByPrice(
            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );

    @GetMapping("/type/{commerceType}/ordered-by-price")
    @Operation(
            summary = "Listar productos por tipo de comercio ordenados por precio",
            description = "Retorna una lista paginada de productos activos filtrados por tipo de comercio y ordenados por precio con descuento de mayor a menor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tipo de comercio inválido")
    })
    Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(
            @Parameter(description = "Tipo de comercio", required = true)
            @PathVariable CommerceTypeEnum commerceType,

            @Parameter(description = "Parámetros de paginación")
            Pageable pageable
    );
}
