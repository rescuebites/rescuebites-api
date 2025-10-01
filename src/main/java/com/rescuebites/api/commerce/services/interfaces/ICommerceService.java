package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public interface ICommerceService {
    //Register Commerce
    void registerCommerce (RegisterCommerceRequest commerce, MultipartFile profilePhoto);

    //Update Commerce
    void updateCommerce(UUID id, UpdateCommerceRequest request);

    //Commerce Profile (GET)
    //CommerceProfileResponse getCommerceProfile(UUID id);
}
