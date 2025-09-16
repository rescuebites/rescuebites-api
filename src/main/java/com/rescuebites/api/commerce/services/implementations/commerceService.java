package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.data.models.commerce;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@RequiredArgsConstructor
public class commerceService implements ICommerceService {
    private final ICommerceRepository commerceRepository;

    //Register Commerce
    public void registerCommerce(RegisterCommerceRequest commerce, MultipartFile photo){
    }

    //Update Commerce
    @Override
    public void updateCommerce(UUID id, UpdateCommerceRequest request) {
        // Obtenemos el comercio (en teoría ya existe porque viene del perfil)
        commerce existingCommerce = commerceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("commerce", "id", id));
        boolean modified = false;

        // Validaciones campo por campo, si el nombre es distinto, se actualiza
        if (request.name() != null && !request.name().isBlank()
                && !request.name().equals(existingCommerce.getName())) {
            existingCommerce.setName(request.name());
            modified = true;
        }

        if (request.description() != null && !request.description().isBlank()
                && !request.description().equals(existingCommerce.getDescription())) {
            existingCommerce.setDescription(request.description());
            modified = true;
        }

        if (request.commerceTypes() != null
                && !request.commerceTypes().equals(existingCommerce.getCommerceTypes())) {
            existingCommerce.setCommerceTypes(request.commerceTypes());
            modified = true;
        }

        if (request.openingHours() != null
                && !request.openingHours().equals(existingCommerce.getOpeningHours())) {
            existingCommerce.setOpeningHours(request.openingHours());
            modified = true;
        }

        if (request.direction() != null
                && !request.direction().equals(existingCommerce.getDirection())) {
            existingCommerce.setDirection(request.direction());
            modified = true;
        }

        if (request.phone() != null
                && !request.phone().equals(existingCommerce.getPhone())) {
            existingCommerce.setPhone(request.phone());
            modified = true;
        }

        if (request.imageUrl() != null
                && !request.imageUrl().equals(existingCommerce.getImageUrl())) {
            existingCommerce.setImageUrl(request.imageUrl());
            modified = true;
        }

        //Si no se modificó nada, lanza una excepción
        if (!modified) {
            throw new RuntimeException("Debe modificar al menos un campo");
        }

        //Guarda los cambios en la base de datos
        commerceRepository.save(existingCommerce);
    }
}
