package com.rescuebites.api.client.data.models;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity(name = "preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    @Id
    @Column(name = "preferenceId")
    private UUID preferenceId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    private PreferenceType preferenceType;

    @ManyToMany(mappedBy = "preferences")
    private List<Client> clients;
}