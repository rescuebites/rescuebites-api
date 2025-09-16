package com.rescuebites.api.commerce.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aspectj.bridge.Message;

import java.util.UUID;

public class CommerceType {
    @Id
    private final UUID id = UUID.randomUUID();

    @NotBlank(message="Nombre obligatorio")
    private String name;

    @NotBlank(message="Descripción obligatorio")
    @Size(min = 1, max = 50, message = "La descripción debe tener entre 1 y 50 caracteres")
    private String description;

    @ManyToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn( name = "commerce_type_id")
    private commerce commerce;
}
