package com.rescuebites.api.data.models;

import com.rescuebites.api.security.enums.PreferencesType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID preferenceId;

    @Enumerated(EnumType.STRING)
    private PreferencesType preferenceType;

}
