package com.rescuebites.api.data.models;

import com.rescuebites.api.client.data.models.Client;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @Column(name = "imageId")
    private UUID imageId = UUID.randomUUID();

    private String url;

    private String publicId;

    @OneToOne(mappedBy = "image")
    private Client client;
}