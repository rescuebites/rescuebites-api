package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.product.controllers.responses.ProductPublicResponse;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IClientProductService;
import com.rescuebites.api.product.services.interfaces.IPublicProductService;
import com.rescuebites.api.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        Client client = fetchAndValidateClient(clientId);
        List<PreferenceType> preferences = client.getPreferences();

        String clientLocalityName = client.getLocality().getName().trim();

        if (preferences.isEmpty()) {
            return publicProductService.getAllActiveProducts(clientLocalityName, pageable);
        }

        return toProductResponsePage(
                productRepository.findActiveProductsWithPreferencesAndLocality(preferences, clientLocalityName, pageable), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsByCommerce(UUID clientId, UUID commerceId, Pageable pageable) {
        Client client = fetchAndValidateClient(clientId);
        List<PreferenceType> preferences = client.getPreferences();
        validationFacade.validateCommerceExists(commerceId);

        String clientLocalityName = client.getLocality().getName().trim();

        // Con localidad: usar queries que filtran por localidad
        if (preferences.isEmpty()) {
            return toProductResponsePage(
                    productRepository.findActiveByCommerceIdAndLocality(commerceId, clientLocalityName, pageable), pageable);
        }

        return toProductResponsePage(
                productRepository.findActiveByCommerceIdWithPreferencesAndLocality(commerceId, preferences, clientLocalityName, pageable), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllActiveProductsOrderedByPrice(UUID clientId, Pageable pageable) {
        Client client = fetchAndValidateClient(clientId);
        List<PreferenceType> preferences = client.getPreferences();

        String clientLocalityName = client.getLocality().getName().trim();

        if (preferences.isEmpty()) {
            return publicProductService.getAllActiveProductsOrderedByPrice(clientLocalityName, pageable);
        }

        return toProductResponsePage(
                productRepository.findActiveProductsWithPreferencesOrderByPriceAndLocality(preferences, clientLocalityName, pageable), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPublicResponse> getActiveProductsByCommerceTypeOrderedByPrice(UUID clientId, CommerceTypeEnum commerceType, Pageable pageable) {
        Client client = fetchAndValidateClient(clientId);
        List<PreferenceType> preferences = client.getPreferences();

        String clientLocalityName = client.getLocality().getName().trim();

        if (preferences.isEmpty()) {
            return publicProductService.getActiveProductsByCommerceTypeOrderedByPrice(commerceType, clientLocalityName, pageable);
        }

        Page<Product> products = productRepository
                .findActiveByCommerceTypeWithPreferencesOrderByPriceAndLocality(commerceType, preferences, clientLocalityName, pageable);
        List<ProductPublicResponse> content = products.getContent().stream()
                .map(ProductMapper::toProductPublicResponse)
                .toList();
        return new PageImpl<>(content, pageable, products.getTotalElements());
    }

    private Page<ProductResponse> toProductResponsePage(Page<Product> products, Pageable pageable) {
        List<ProductResponse> content = products.getContent().stream()
                .map(ProductMapper::toProductResponse)
                .toList();
        return new PageImpl<>(content, pageable, products.getTotalElements());
    }

    private Client fetchAndValidateClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "clientId", clientId));

        SecurityUtils.validateOwnership(client.getUser().getEmail());
        return client;
    }
}
