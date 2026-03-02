package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommerceRequest {

    @NotNull(message = "El usuario es obligatorio")
    private UUID userId;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;

    @NotEmpty(message = "Debe seleccionar al menos un tipo de comercio")
    private List<CommerceTypeEnum> commerceTypes;

    @NotEmpty(message = "Debe definir al menos un horario de atención")
    @Valid
    private List<BusinessHoursRequest> businessHours;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La localidad es obligatoria")
    private String locality;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^\\+54(9)?[0-9]{10}$",
            message = "El número de celular debe tener el formato válido argentino, ej: +549XXXXXXXX"
    )
    private String phone;
}