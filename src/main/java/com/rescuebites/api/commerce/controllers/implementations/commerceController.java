package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.commerce.controllers.interfaces.ICommerceController;
import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.rescuebites.api.commerce.services.implementations.commerceService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class commerceController implements ICommerceController {
    private final ICommerceService commerceService;

    //Register Commerce
    @Override
    public void registerCommerce( RegisterCommerceRequest registerCommerceRequest,
                                  MultipartFile profilePicture){
        commerceService.registerCommerce(registerCommerceRequest, profilePicture);
    }
    //Update Commerce
    @Override
    public void updateCommerce(UUID id, UpdateCommerceRequest request) {
        commerceService.updateCommerce(id, request);
    }
}
