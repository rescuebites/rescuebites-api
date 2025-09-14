package com.rescuebites.api.data.models;

import com.rescuebites.api.security.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data //Incluye getters, setters, toString, equals, and hashCode methods
@Builder
public class User {

    @Id
    @Column(name = "userId")
    private UUID userId = UUID.randomUUID();

    //private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.CLIENT; // El rol por defecto es CLIENT

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Token> tokens = new ArrayList<>(); // Inicializamos la lista como una lista inmutable vacía (not nulls)

    @Builder.Default
    private boolean enabled = false; //Se setea en true cuando el usuario confirma su email
}