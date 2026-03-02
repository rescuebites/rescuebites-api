package com.rescuebites.api.commerce.data.models;

import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name = "commerces")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commerce {

    @Id
    @Column(name = "commerce_id")
    @Builder.Default
    private UUID commerceId = UUID.randomUUID();

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @Size(max = 255, message = "La descripción debe tener entre 1 y 255 caracteres")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "commerce_commerce_types",
            joinColumns = @JoinColumn(name = "commerce_id"),
            inverseJoinColumns = @JoinColumn(name = "commerce_type_id")
    )
    @Builder.Default
    private List<CommerceType> commerceTypes = new ArrayList<>();

    @OneToMany(mappedBy = "commerce", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<BusinessHours> businessHours = new HashSet<>();

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "La localidad es obligatoria")
    @Column(nullable = false)
    private String locality;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^\\+54(9)?[0-9]{10}$",
            message = "El número de celular debe tener el formato válido argentino"
    )
    private String phone;

    @Column(name = "mercado_pago_access_token")
    private String mercadoPagoAccessToken;

    @Column(name = "mercado_pago_webhook_secret")
    private String mercadoPagoWebhookSecret;

    @OneToMany(mappedBy = "commerce", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "commerce", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}