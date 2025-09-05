package com.rescuebites.api.data.models;

import com.rescuebites.api.security.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data //Incluye getters, setters, toString, equals, and hashCode methods
@Builder
public class User {

    @Id
    private final UUID id = UUID.randomUUID();

    private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER; // El rol por defecto es USER

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Token> tokens = Collections.emptyList(); // Inicializamos la lista como una lista inmutable vacía (not nulls)

    private boolean enabled = false; //Se setea en true cuando el usuario confirma su email
}