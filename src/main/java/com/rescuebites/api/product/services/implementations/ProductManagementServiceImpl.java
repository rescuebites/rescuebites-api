package com.rescuebites.api.product.services.implementations;

import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.product.controllers.requests.CreateProductRequest;
import com.rescuebites.api.product.controllers.requests.UpdateProductRequest;
import com.rescuebites.api.product.controllers.responses.ProductResponse;
import com.rescuebites.api.product.data.enums.ProductCategory;
import com.rescuebites.api.product.data.enums.ProductCondition;
import com.rescuebites.api.product.data.mappers.ProductMapper;
import com.rescuebites.api.product.data.models.Product;
import com.rescuebites.api.product.facades.interfaces.IProductValidationFacade;
import com.rescuebites.api.product.repositories.IProductRepository;
import com.rescuebites.api.product.services.interfaces.IProductManagementService;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductManagementServiceImpl implements IProductManagementService {

    private final IProductRepository productRepository;
    private final IImageFacade imageFacade;
    private final IProductValidationFacade validationFacade;

    @Override
    @Transactional
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {

        Commerce commerce = validationFacade.findCommerceById(commerceId);

        validationFacade.validateExpirationDate(request.expirationDate());
        imageFacade.validateImages(images);
        validationFacade.validateCategoryAndCondition(request.category(), request.condition(), commerce);

        List<Image> storedImages = imageFacade.uploadAndSaveImages(images);

        Product product = ProductMapper.toProduct(request, commerce, storedImages);
        commerce.getProducts().add(product);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public Page<ProductResponse> getProductsByCommerce(UUID commerceId, Pageable pageable) {

        validationFacade.validateCommerceExists(commerceId);

        Page<Product> products = productRepository.findByCommerceId(commerceId, pageable);

        return products.map(ProductMapper::toProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse getProductByCommerceAndId(UUID commerceId, UUID productId) {

        Product product = productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        return ProductMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void updateProduct(UUID commerceId, UUID productId, UpdateProductRequest request, MultipartFile[] images) {

        Product product = productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (request.expirationDate() != null) {
            validationFacade.validateExpirationDate(request.expirationDate());
        }

        if (request.category() != null || request.condition() != null) {
            ProductCategory categoryToValidate = request.category() != null ?
                    request.category() : product.getCategory();
            ProductCondition conditionToValidate = request.condition() != null ?
                    request.condition() : product.getCondition();

            validationFacade.validateCategoryAndCondition(categoryToValidate, conditionToValidate, product.getCommerce());
        }

        updateProductFields(product, request);

        if (images != null && images.length > 0) {
            List<Image> newImages = imageFacade.processAndUpdateImages(product.getImages(), images);
            product.getImages().clear();
            product.setImages(newImages);
        }

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID commerceId, UUID productId) {

        Product product = productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void activateProduct(UUID commerceId, UUID productId) {
        Product product = productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(UUID commerceId, UUID productId) {
        Product product = productRepository
                .findByIdAndCommerceId(productId, commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setActive(false);
        productRepository.save(product);
    }

    private void updateProductFields(Product product, UpdateProductRequest request) {
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.stock() != null) {
            product.setStock(request.stock());
        }
        if (request.originalPrice() != null) {
            product.setOriginalPrice(request.originalPrice());
        }
        if (request.discountPercentage() != null) {
            product.setDiscountPercentage(request.discountPercentage());
        }
        if (request.category() != null) {
            product.setCategory(request.category());
        }
        if (request.condition() != null) {
            product.setCondition(request.condition());
        }
        if (request.expirationDate() != null) {
            product.setExpirationDate(request.expirationDate());
        }
    }
}