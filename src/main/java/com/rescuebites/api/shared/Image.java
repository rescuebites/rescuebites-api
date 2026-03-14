package com.rescuebites.api.shared;

import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.product.data.models.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity(name = "images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Image {

    @Id
    @Column(name = "imageId")
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID imageId = UUID.randomUUID();

    private String url;

    private String publicId;

    @OneToOne(mappedBy = "image")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "commerce_id")
    private Commerce commerce;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}