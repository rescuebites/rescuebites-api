package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rescuebites.api.users.data.mappers.UserMapper.toUserResponse;

@Component
public class CommerceMapper {

    public static Commerce toCommerce(
            CreateCommerceRequest request,
            User user,
            List<CommerceType> commerceTypes,
            List<Image> images
    ) {
        Commerce commerce = Commerce.builder()
                .commerceId(UUID.randomUUID())
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .commerceTypes(commerceTypes)
                .openingHours(request.getOpeningHours())
                .address(request.getAddress())
                .locality(request.getLocality())
                .phone(request.getPhone())
                .build();

        // Asociar las imágenes al comercio
        images.forEach(image -> image.setCommerce(commerce));
        commerce.setImages(images);

        return commerce;
    }

    public static CommerceResponse toCommerceResponse(Commerce commerce) {
        return new CommerceResponse(
                commerce.getCommerceId(),
                commerce.getName(),
                commerce.getDescription(),
                commerce.getCommerceTypes().stream()
                        .map(CommerceType::getName)
                        .collect(Collectors.toList()),
                commerce.getOpeningHours(),
                commerce.getAddress(),
                commerce.getLocality(),
                commerce.getPhone(),
                commerce.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList()),
                toUserResponse(commerce.getUser())
        );
    }

    public static void updateCommerceFromRequest(
            Commerce commerce,
            UpdateCommerceRequest request,
            List<CommerceType> commerceTypes,
            List<Image> newImages
    ) {
        if (request.getName() != null) {
            commerce.setName(request.getName());
        }
        if (request.getDescription() != null) {
            commerce.setDescription(request.getDescription());
        }
        if (commerceTypes != null && !commerceTypes.isEmpty()) {
            commerce.setCommerceTypes(commerceTypes);
        }
        if (request.getOpeningHours() != null) {
            commerce.setOpeningHours(request.getOpeningHours());
        }
        if (request.getAddress() != null) {
            commerce.setAddress(request.getAddress());
        }
        if (request.getLocality() != null) {
            commerce.setLocality(request.getLocality());
        }
        if (request.getPhone() != null) {
            commerce.setPhone(request.getPhone());
        }
        if (newImages != null && !newImages.isEmpty()) {
            commerce.getImages().clear();
            newImages.forEach(image -> {
                image.setCommerce(commerce);
                commerce.getImages().add(image);
            });
        }
    }
}