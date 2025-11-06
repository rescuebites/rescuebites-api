package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.requests.CreateProductRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/api/v1/commerce/{commerceId}/products")
public interface IProductController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    void createProduct(@PathVariable UUID commerceId,
                       @RequestPart("product") @Valid CreateProductRequest request,
                       @RequestPart("images") MultipartFile[] images);
}
