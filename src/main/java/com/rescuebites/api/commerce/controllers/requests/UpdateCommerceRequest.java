package com.rescuebites.api.commerce.controllers.requests;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommerceRequest {

    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String description;

    private List<CommerceTypeEnum> commerceTypes;

    private String openingHours;

    private String address;

    private String locality;

    @Pattern(
            regexp = "^\\+54(9)?[0-9]{10}$",
            message = "El número de celular debe tener el formato válido argentino, ej: +54911XXXXXXXX"
    )
    private String phone;

    @Email(message = "El email debe ser válido")
    private String email;

    private String password;

    private String confirmPassword;
}