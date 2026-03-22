package com.rescuebites.api.users.data.models;

import com.rescuebites.api.security.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @Column(name = "userId")
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID userId = UUID.randomUUID();

    @ToString.Include
    private String email;

    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.CLIENT;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Token> tokens = new ArrayList<>();

    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    /** Email pendiente de confirmación (usado durante el flujo de cambio de email). */
    @Column(name = "pending_email")
    private String pendingEmail;
}