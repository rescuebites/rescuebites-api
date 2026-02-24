package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IClientProductService;
import com.rescuebites.api.product.services.interfaces.IPublicProductService;
import com.rescuebites.api.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientProductServiceImpl implements IClientProductService {

    private final IProductRepository productRepository;
    private final IClientRepository clientRepository;
    private final IProductValidationFacade validationFacade;
    private final IPublicProductService publicProductService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsMatchingClientPreferences(UUID clientId, Pageable pageable) {
        List<PreferenceType> preferences = getClientPreferences(clientId);

        if (preferences.isEmpty()) {
            return publicProductService.getAllActiveProducts(pageable);
        }

        return productRepository.findActiveProductsWithPreferences(preferences, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsByCommerce(UUID clientId, UUID commerceId, Pageable pageable) {
        List<PreferenceType> preferences = getClientPreferences(clientId);
        validationFacade.validateCommerceExists(commerceId);

        if (preferences.isEmpty()) {
            return publicProductService.getActiveProductsByCommerce(commerceId, pageable);
        }

        return productRepository.findActiveByCommerceIdWithPreferences(commerceId, preferences, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(UUID clientId, Pageable pageable) {
        List<PreferenceType> preferences = getClientPreferences(clientId);

        if (preferences.isEmpty()) {
            return publicProductService.getAllActiveProductsOrderedByPrice(pageable);
        }

        return productRepository.findActiveProductsWithPreferencesOrderByPrice(preferences, pageable)
                .map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(UUID clientId, CommerceTypeEnum commerceType, Pageable pageable) {
        List<PreferenceType> preferences = getClientPreferences(clientId);

        if (preferences.isEmpty()) {
            return publicProductService.getActiveProductsByCommerceTypeOrderedByPrice(commerceType, pageable);
        }

        return productRepository.findActiveByCommerceTypeWithPreferencesOrderByPrice(commerceType, preferences, pageable)
                .map(ProductMapper::toProductPublicResponse);
    }

    private List<PreferenceType> getClientPreferences(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));

        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return client.getPreferences() != null
                ? client.getPreferences()
                : List.of();
    }
}
