package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ICommerceService {

    void createCommerce(CreateCommerceRequest createCommerceRequest, MultipartFile[] images);

    CommerceResponse getCommerceById(UUID commerceId);

    void updateCommerce(UUID commerceId, UpdateCommerceRequest updateCommerceRequest, MultipartFile[] images);

    void deleteCommerce(UUID commerceId);

    void updateCommerceCredentials(UUID commerceId, com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest request);
}