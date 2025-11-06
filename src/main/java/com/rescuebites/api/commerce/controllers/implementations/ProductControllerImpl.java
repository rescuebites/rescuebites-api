package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.IProductController;
import com.rescuebites.api.commerce.controllers.requests.CreateProductRequest;
import com.rescuebites.api.commerce.services.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements IProductController {

    private final IProductService productService;

    @Override
    public void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images) {
        productService.createProduct(commerceId, request, images);
    }
}
