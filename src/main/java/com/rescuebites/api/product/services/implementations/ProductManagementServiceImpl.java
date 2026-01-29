package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.commerce.data.models.Commerce;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {
        Commerce commerce = productValidationFacade.findCommerceById(commerceId);

        SecurityUtils.validateOwnership(commerce.getUser().getEmail());
        productValidationFacade.validateExpirationDate(request.getExpirationDate());
        imageFacade.validateImages(images);
        productValidationFacade.validateCategoryAndCondition(
                request.getCategory(),
                request.getCondition(),
                commerce
        );

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
    @Transactional
    public Page<ProductResponse> getProductsByCommerce(UUID commerceId, Pageable pageable) {
        Commerce commerce = productValidationFacade.findCommerceById(commerceId);
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());

        Page<Product> products = productRepository.findByCommerceId(
                commerceId,
                pageable
        );
        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse getProductByCommerceAndId(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void updateProduct(UUID commerceId, UUID productId, UpdateProductRequest request, MultipartFile[] images) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        productValidationFacade.validateAtLeastOneFieldToUpdate(request);
        productValidationFacade.validateExpirationDate(request.getExpirationDate());
        productValidationFacade.validateAndProcessCategoryConditionUpdate(
                product,
                request
        );

        List<Image> newImages = imageFacade.processImagesIfProvided(
                product.getImages(),
                images
        );

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
    public void activateProduct(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(UUID commerceId, UUID productId) {
        Product product = productValidationFacade.findProductByIdAndCommerceIdOrThrowException(productId, commerceId);
        SecurityUtils.validateOwnership(product.getCommerce().getUser().getEmail());

        product.setActive(false);
        productRepository.save(product);
    }
}