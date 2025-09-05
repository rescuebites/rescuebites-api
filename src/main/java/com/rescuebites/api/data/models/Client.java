package com.rescuebites.api.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientId;

    private String name;
    private String lastName;
    private String dateBirth;
    private String email;
    private String password;
    private String imageUrl;
    private String address;

    @ManyToMany
    @JoinTable(
            name = "clientPreferences",
            joinColumns = @JoinColumn(name = "clientId"),
            inverseJoinColumns = @JoinColumn(name = "preferenceId")
    )
    private List<Preference> preferences = new ArrayList<>();

}
