package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.models.BusinessHours;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .address(request.getAddress())
                .locality(request.getLocality())
                .phone(request.getPhone())
                .build();

        // Asociar horarios al comercio
        List<BusinessHours> businessHours = BusinessHoursMapper.toBusinessHoursList(
                request.getBusinessHours(), commerce);
        commerce.getBusinessHours().addAll(businessHours);

        // Asociar las imágenes al comercio
        images.forEach(image -> image.setCommerce(commerce));
        commerce.setImages(images);

        return commerce;
    }

    public static CommerceResponse toCommerceResponse(Commerce commerce) {
        return new CommerceResponse(
                commerce.getName(),
                commerce.getDescription(),
                commerce.getCommerceTypes().stream()
                        .map(CommerceType::getName)
                        .collect(Collectors.toList()),
                BusinessHoursMapper.toBusinessHoursResponseList(commerce.getBusinessHours()),
                commerce.getAddress(),
                commerce.getLocality(),
                commerce.getPhone(),
                commerce.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList())
        );
    }

    public static CommercePublicResponse toCommercePublicResponse(Commerce commerce) {
        return new CommercePublicResponse(
                commerce.getCommerceId(),
                commerce.getName(),
                commerce.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList())
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
        if (request.getBusinessHours() != null && !request.getBusinessHours().isEmpty()) {
            Map<DayOfWeek, BusinessHours> existingByDay = commerce.getBusinessHours().stream()
                    .collect(Collectors.toMap(BusinessHours::getDayOfWeek, bh -> bh));

            for (BusinessHoursRequest bhRequest : request.getBusinessHours()) {
                BusinessHours existing = existingByDay.get(bhRequest.getDayOfWeek());
                if (existing != null) {
                    // Actualizar el día existente
                    existing.setClosed(bhRequest.isClosed());
                    existing.setOpenTime(bhRequest.getOpenTime());
                    existing.setCloseTime(bhRequest.getCloseTime());
                    existing.setAfternoonOpenTime(bhRequest.getAfternoonOpenTime());
                    existing.setAfternoonCloseTime(bhRequest.getAfternoonCloseTime());
                } else {
                    // Agregar nuevo día
                    commerce.getBusinessHours().add(
                            BusinessHoursMapper.toBusinessHours(bhRequest, commerce));
                }
            }
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