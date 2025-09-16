package com.rescuebites.api.commerce.data.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "commerce")
@AllArgsConstructor
@NoArgsConstructor
@Data //Incluye getters, setters, toString, equals, y hashCode methods
@Builder
public class commerce {
    @Id
    private final UUID id = UUID.randomUUID();

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Size(min = 1, max = 50, message = "La descripción debe tener entre 1 y 50 caracteres")
    private String description;

    @NotBlank(message = "El tipo de comercio es obligatorio")
    @OneToMany(
            mappedBy = "commerce",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<CommerceType> commerceTypes;

    @NotBlank(message = "El horario es obligatorio")
    private String openingHours;

    @NotBlank(message = "La dirección es obligatoria")
    private String direction;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^\\+54(9)?[0-9]{10}$",
            message = "El número de celular debe tener el formato válido argentino, ej: +54911XXXXXXXX"
    )
    private String phone;

    @NotBlank(message = "La foto es obligatoria")
    private String imageUrl; // Guardaría URL

    //Estado: activo/inactivo (para borraod lógico)
    @Builder.Default
    private boolean active = false; // Inicialmente inactivo hasta validación de email
}
