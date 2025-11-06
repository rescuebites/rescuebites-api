package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "commerce_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommerceType {

    @Id
    @Column(name = "commerce_type_id")
    @Builder.Default
    private UUID commerceTypeId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private CommerceTypeEnum name;

    @ManyToMany(mappedBy = "commerceTypes")
    @Builder.Default
    private List<Commerce> commerces = new ArrayList<>();
}
