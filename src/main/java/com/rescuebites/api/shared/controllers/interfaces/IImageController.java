package com.rescuebites.api.shared.controllers.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequestMapping("/api/v1/images")
@Tag(name = "Gestión de Imágenes", description = "Endpoints para gestión centralizada de imágenes")
public interface IImageController {

    @DeleteMapping("/{imageId}")
    @ResponseStatus(NO_CONTENT)
    @Operation(
            summary = "Eliminar una imagen",
            description = "Elimina una imagen del sistema. El sistema detecta automáticamente " +
                    "si la imagen pertenece a un producto, comercio o cliente y valida los permisos " +
                    "correspondientes. Se requiere autenticación."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imagen eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar (última imagen requerida)"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar esta imagen"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    void deleteImage(@PathVariable UUID imageId);
}
