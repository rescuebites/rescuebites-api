package com.rescuebites.api.product.controllers.interfaces;

import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/api/v1/commerces/{commerceId}/products")
@Tag(name = "Products", description = "Gestión de productos del comercio")
public interface IProductManagementController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    @Operation(
            summary = "Crear nuevo producto",
            description = "Crea un producto asociado a un comercio específico con sus imágenes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    void createProduct(
            @Parameter(description = "ID del comercio", required = true)
            @PathVariable UUID commerceId,

            @Parameter(description = "Datos del producto en formato JSON", required = true)
            @RequestPart("product") @Valid CreateProductRequest request,

            @Parameter(description = "Imágenes del producto (mínimo 1, máximo 5)", required = true)
            @RequestPart("images") MultipartFile[] images
    );

    @GetMapping
    @Operation(summary = "Listar productos del comercio (activo o inactivo)")
    Page<ProductResponse> getProductsByCommerce(
            @PathVariable UUID commerceId,
            Pageable pageable
    );

    @GetMapping("/{productId}")
    @Operation(summary = "Obtener detalle de un producto (activo o inactivo)")
    ProductResponse getProduct(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId
    );

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar un producto")
    void updateProduct(@PathVariable UUID commerceId,
                       @PathVariable UUID productId,
                       @RequestPart("product") @Valid UpdateProductRequest request,
                       @RequestPart(value = "images", required = false) MultipartFile[] images);

    @DeleteMapping("/{productId}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Eliminar producto")
    void deleteProduct(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId
    );

    @PatchMapping("/{productId}/activate")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Activar producto")
    void activateProduct(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId
    );

    @PatchMapping("/{productId}/deactivate")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Desactivar producto")
    void deactivateProduct(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId
    );
}