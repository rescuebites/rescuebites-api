package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.client.data.enums.PreferenceType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "package_deals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID packageId;

    @NotBlank(message = "El nombre del paquete es obligatorio")
    private String name;

    @NotBlank(message = "La descripción del paquete es obligatoria")
    private String description;

    @NotNull(message = "El identificador del comercio es obligatorio")
    private UUID commerceId;

    @Builder.Default
    @ElementCollection(targetClass = PreferenceType.class)
    @CollectionTable(name = "package_deal_preferences", joinColumns = @JoinColumn(name = "package_id"))
    @Enumerated(EnumType.STRING)
    private List<PreferenceType> applicablePreferences = new ArrayList<>();

    @Builder.Default
    private boolean available = true;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime availableUntil;
}
