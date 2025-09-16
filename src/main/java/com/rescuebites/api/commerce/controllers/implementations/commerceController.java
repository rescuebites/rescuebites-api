package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.ICommerceController;
import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import org.springframework.web.multipart.MultipartFile;
import com.rescuebites.api.commerce.services.implementations.commerceService;
import com.rescuebites.api.commerce.data.models.commerce;

import java.util.UUID;

public class commerceController implements ICommerceController {
    private final ICommerceService commerceService;

    public commerceController(commerceService commerceService){
        this.commerceService = commerceService;
    }

    //Register Commerce
    public void registerCommerce( RegisterCommerceRequest registerCommerceRequest,
                                  MultipartFile photo){
        commerceService.registerCommerce(registerCommerceRequest, photo);
    }
    //Update Commerce
    @Override
    public void updateCommerce(UUID id, UpdateCommerceRequest request) {
        commerceService.updateCommerce(id, request);
    }
}
