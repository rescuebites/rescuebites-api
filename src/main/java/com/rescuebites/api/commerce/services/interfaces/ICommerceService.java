package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceIdentityCheckResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ICommerceService {

    void createCommerce(CreateCommerceRequest createCommerceRequest, MultipartFile[] images);

    void updateCommerce(UUID commerceId, UpdateCommerceRequest updateCommerceRequest, MultipartFile[] images);

    void deleteCommerce(UUID commerceId);

    void updateCommerceCredentials(UUID commerceId, com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest request);

    CommerceIdentityCheckResponse checkIdentityAvailability(String name, String address, String locality, UUID excludeCommerceId);

    List<String> validateBusinessHours(List<BusinessHoursRequest> businessHours);
}