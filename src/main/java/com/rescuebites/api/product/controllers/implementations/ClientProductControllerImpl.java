package com.rescuebites.api.product.controllers.implementations;

import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.product.controllers.interfaces.IClientProductController;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.services.interfaces.IClientProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientProductControllerImpl implements IClientProductController {

    private final IClientProductService clientProductService;

    @Override
    public Page<ProductResponse> getProductsMatchingClientPreferences(UUID clientId, Pageable pageable) {
        return clientProductService.getProductsMatchingClientPreferences(clientId, pageable);
    }

    @Override
    public Page<ProductResponse> getActiveProductsByCommerce(UUID clientId, UUID commerceId, Pageable pageable) {
        return clientProductService.getActiveProductsByCommerce(clientId, commerceId, pageable);
    }

    @Override
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(UUID clientId, Pageable pageable) {
        return clientProductService.getAllActiveProductsOrderedByPrice(clientId, pageable);
    }

    @Override
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(UUID clientId, CommerceTypeEnum commerceType, Pageable pageable) {
        return clientProductService.getActiveProductsByCommerceTypeOrderedByPrice(clientId, commerceType, pageable);
    }
}
