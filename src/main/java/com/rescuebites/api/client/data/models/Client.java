package com.rescuebites.api.client.data.models;

import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @Column(name = "clientId")
    private UUID clientId = UUID.randomUUID();

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    //@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, orphanRemoval = true)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imageId", referencedColumnName = "imageId")
    private Image image;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "clientPreferences",
            joinColumns = @JoinColumn(name = "clientId"),
            inverseJoinColumns = @JoinColumn(name = "preferenceId")
    )
    private List<Preference> preferences = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}