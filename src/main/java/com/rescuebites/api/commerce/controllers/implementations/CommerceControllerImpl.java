package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.ICommerceController;
import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.BusinessHoursValidationResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceIdentityCheckResponse;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommerceControllerImpl implements ICommerceController {

    private final ICommerceService commerceService;

    @Override
    public void createCommerce(CreateCommerceRequest createCommerceRequest, MultipartFile[] images) {
        commerceService.createCommerce(createCommerceRequest, images);
    }

    @Override
    public void updateCommerce(UUID commerceId, UpdateCommerceRequest updateCommerceRequest, MultipartFile[] images) {
        commerceService.updateCommerce(commerceId, updateCommerceRequest, images);
    }

    @Override
    public void deleteCommerce(UUID commerceId) {
        commerceService.deleteCommerce(commerceId);
    }

    @Override
    public void updateCredentials(UUID commerceId, UpdateCommerceCredentialsRequest request) {
        commerceService.updateCommerceCredentials(commerceId, request);
    }

    @Override
    public CommerceIdentityCheckResponse checkIdentityAvailability(String name, String address, String locality, UUID excludeCommerceId) {
        return commerceService.checkIdentityAvailability(name, address, locality, excludeCommerceId);
    }

    @Override
    public BusinessHoursValidationResponse validateBusinessHours(List<BusinessHoursRequest> businessHours) {
        List<String> errors = commerceService.validateBusinessHours(businessHours != null ? businessHours : Collections.emptyList());
        return BusinessHoursValidationResponse.of(errors);
    }
}