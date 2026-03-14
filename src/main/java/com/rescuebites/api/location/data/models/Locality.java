package com.rescuebites.api.location.data.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity(name = "localities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Locality {

    @Id
    @Column(name = "locality_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID localityId = UUID.randomUUID();

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;
}
