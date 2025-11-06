package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.requests.CreateProductRequest;
import com.rescuebites.api.commerce.data.enums.ProductCheckType;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.data.models.Product;
import com.rescuebites.api.commerce.data.mappers.ProductMapper;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.repositories.IProductRepository;
import com.rescuebites.api.commerce.services.interfaces.IProductService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ICommerceRepository commerceRepository;
    private final IProductRepository productRepository;
    private final IImageFacade imageFacade;
    private final Clock clock;

    @Override
    @Transactional
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {

        Commerce commerce = commerceRepository.findByCommerceIdAndActiveTrue(commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Commerce", "id", commerceId));

        validateExpirationDate(request.expirationDate());
        validateImages(images);
        validateChecks(request.checks(), commerce);

        List<Image> storedImages = imageFacade.uploadAndSaveImages(images);

        Product product = ProductMapper.toProduct(request, commerce, storedImages);
        commerce.getProducts().add(product);

        productRepository.save(product);
    }

    private void validateChecks(List<ProductCheckType> checks, Commerce commerce) {
        if (checks == null || checks.isEmpty()) {
            throw new ValidationException("Debe seleccionar al menos un check");
        }

        Set<com.rescuebites.api.commerce.data.enums.CommerceTypeEnum> commerceTypes = commerce.getCommerceTypes().stream()
                .map(CommerceType::getName)
                .collect(Collectors.toSet());

        boolean allChecksAllowed = checks.stream()
                .allMatch(check -> commerceTypes.stream().anyMatch(check::isAllowedFor));

        if (!allChecksAllowed) {
            throw new ValidationException("Los checks seleccionados no son válidos para el tipo de comercio");
        }
    }

    private void validateImages(MultipartFile[] images) {
        if (images == null || images.length == 0) {
            throw new ValidationException("Debe cargar al menos una foto del producto");
        }

        Arrays.stream(images).forEach(image -> {
            if (image == null || image.isEmpty()) {
                throw new ValidationException("Todas las imágenes del producto deben ser válidas");
            }
            imageFacade.ifProfilePictureExceedsMaximumSizeThrowException(image);
            imageFacade.ifProfilePictureIsNotJpgOrPngThrowException(image.getContentType());
        });
    }

    private void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            return;
        }

        LocalDate today = LocalDate.now(clock);
        if (expirationDate.isBefore(today)) {
            throw new ValidationException("La fecha de vencimiento no puede ser anterior a hoy");
        }
    }
}
