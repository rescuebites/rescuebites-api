package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "commerce_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CommerceType {

    @Id
    @Column(name = "commerce_type_id")
    @Builder.Default
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID commerceTypeId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private CommerceTypeEnum name;

    @ManyToMany(mappedBy = "commerceTypes")
    @Builder.Default
    private List<Commerce> commerces = new ArrayList<>();
}
