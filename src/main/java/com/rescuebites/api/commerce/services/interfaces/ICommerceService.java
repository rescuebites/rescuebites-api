package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public interface ICommerceService {
    //Register Commerce
    void registerCommerce (RegisterCommerceRequest commerce, MultipartFile photo);
    //Update Commerce
    void updateCommerce(UUID id, UpdateCommerceRequest request);
}
