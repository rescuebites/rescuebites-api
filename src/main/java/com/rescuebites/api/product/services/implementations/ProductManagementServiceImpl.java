package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IProductManagementService;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductManagementServiceImpl implements IProductManagementService {

    private final IProductRepository productRepository;
    private final IImageFacade imageFacade;
    private final IProductValidationFacade productValidationFacade;
    private final ICommerceFacade commerceFacade;

    @Override
    @Transactional
    @CacheEvict(
            value = {"activeProducts", "productsByCommerce", "productById",
                     "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice"},
            allEntries = true
    )
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);

        SecurityUtils.validateOwnership(commerce.getUser().getEmail());
        productValidationFacade.validateExpirationDate(request.getExpirationDate());
        imageFacade.validateImages(images);
        productValidationFacade.validateCategoryAndConditions(
                request.getCategory(),
                request.getConditions(),
                commerce
        );

        // Evitar productos idénticos dentro del mismo comercio
        productValidationFacade.validateNoIdenticalProductInCommerceForCreate(commerceId, request);

        List<Image> storedImages = imageFacade.uploadAndSaveImages(images);

        Product product = ProductMapper.toProduct(
                request,
                commerce,
                storedImages,
                request.getPreferences()
        );

        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCommerce(UUID commerceId, Pageable pageable) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());

        Page<UUID> idsPage = productRepository.findIdsByCommerceId(commerceId, pageable);

        List<ProductResponse> content;
        if (idsPage.isEmpty()) {
            content = List.of();
        } else {
            var products = productRepository.findProductsWithDetailsByIds(idsPage.getContent());
            var byId = products.stream().collect(java.util.stream.Collectors.toMap(Product::getProductId, p -> p));
            content = idsPage.getContent().stream()
                    .map(byId::get)
                    .filter(java.util.Objects::nonNull)
                    .map(ProductMapper::toProductResponse)
                    .toList();
        }

        return new PageImpl<>(content, pageable, idsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByCommerceAndId(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"activeProducts", "productsByCommerce", "productById",
                     "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice"},
            allEntries = true
    )
    public void updateProduct(UUID commerceId, UUID productId, UpdateProductRequest request, MultipartFile[] images) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        productValidationFacade.validateAtLeastOneFieldToUpdate(request);
        productValidationFacade.validateExpirationDate(request.getExpirationDate());

        productValidationFacade.validateAndProcessCategoryConditionUpdate(
                product,
                request
        );

        // Evitar que la actualización deje un producto idéntico a otro dentro del comercio
        productValidationFacade.validateNoIdenticalProductInCommerceForUpdate(commerceId, productId, product, request);

        List<Image> newImages = imageFacade.processImagesIfProvided(
                product.getImages(),
                images
        );

        if (request.getStock() != null &&
            product.getStock() == 0 &&
            request.getStock() > 0 &&
            !product.getActive()) {
            product.setActive(true);
        }

        ProductMapper.updateProductFromRequest(
                product, request,
                request.getPreferences(),
                newImages
        );
        product.setUpdateAt(LocalDateTime.now());

        productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"activeProducts", "productsByCommerce", "productById",
                     "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice"},
            allEntries = true
    )
    public void activateProduct(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = {"activeProducts", "productsByCommerce", "productById",
                     "activeProductsSortedByPrice", "activeProductsByCommerceTypeSortedByPrice"},
            allEntries = true
    )
    public void deactivateProduct(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        product.setActive(false);
        productRepository.save(product);
    }
}