package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.IPublicCommerceController;
import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.services.interfaces.IPublicCommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PublicCommerceControllerImpl implements IPublicCommerceController {

    private final IPublicCommerceService publicCommerceService;

    @Override
    public Page<CommercePublicResponse> getAllCommerces(String locality, Pageable pageable) {
        return publicCommerceService.getAllCommerces(locality, pageable);
    }

    @Override
    public CommerceResponse getCommerceById(UUID commerceId) {
        return publicCommerceService.getCommerceById(commerceId);
    }

    @Override
    public Page<CommercePublicResponse> getCommercesByType(
            CommerceTypeEnum commerceType,
            String locality,
            Pageable pageable
    ) {
        return publicCommerceService.getCommercesByType(commerceType, locality, pageable);
    }
}