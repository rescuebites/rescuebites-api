package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.requests.CreateProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IProductService {

    void createProduct(UUID commerceId, CreateProductRequest request, MultipartFile[] images);
}
