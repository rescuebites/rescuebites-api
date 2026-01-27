package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.IPublicCommerceController;
import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.services.interfaces.IPublicCommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicCommerceControllerImpl implements IPublicCommerceController {

    private final IPublicCommerceService publicCommerceService;

    @Override
    public Page<CommercePublicResponse> getCommercesByType(
            CommerceTypeEnum commerceType,
            Pageable pageable
    ) {
        return publicCommerceService.getCommercesByType(commerceType, pageable);
    }
}