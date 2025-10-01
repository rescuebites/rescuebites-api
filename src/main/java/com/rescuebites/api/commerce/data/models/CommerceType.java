package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.LifecycleState;

import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Entity(name = "commerceTypes")
@Data //Incluye getters, setters, toString, equals, y hashCode methods
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommerceType {
    @Id
    @Column (name = "commerceTypeId")
    private UUID commerceTypeId = UUID.randomUUID();

//    @NotBlank(message="Descripción obligatorio")
//    @Size(min = 1, max = 50, message = "La descripción debe tener entre 1 y 50 caracteres")
//    private String description;

    @ManyToMany(mappedBy = "commerceTypes")
    private List<Commerce> commerces;

    @Enumerated(EnumType.STRING)
    private CommerceTypeEnum commerceTypeEnum;
}
