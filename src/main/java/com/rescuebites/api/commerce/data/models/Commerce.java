package com.rescuebites.api.commerce.data.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "commerce")
@AllArgsConstructor
@NoArgsConstructor
@Data //Incluye getters, setters, toString, equals, y hashCode methods
@Builder
public class Commerce {
    @Id
    @Column(name = "commerceId")
    private UUID commerceId = UUID.randomUUID();

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Size(min = 1, max = 50, message = "La descripción debe tener entre 1 y 50 caracteres")
    private String description;

    @NotEmpty(message = "El tipo de comercio es obligatorio")
    @ManyToMany
    @JoinTable(
            name = "commerce_commerceTypes",
            joinColumns = @JoinColumn(name = "commerceId"),
            inverseJoinColumns = @JoinColumn(name = "commerceTypeId")
    )
    private List<CommerceType> commerceTypes = new ArrayList<>();

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imageId", referencedColumnName = "imageId")
    @JsonManagedReference
    private Image image;

    //Estado: activo/inactivo (para borraod lógico)
    @Builder.Default
    private boolean active = false; // Inicialmente inactivo hasta validación de email
}
