package com.rescuebites.api.commerce.controllers.interfaces;


import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping("/api/v1/commerce")
public interface ICommerceController {
    //Register Commerce
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void registerCommerce(@RequestPart("commerce") @Valid RegisterCommerceRequest registerCommerceRequest, @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture);

    //Update Commerce
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void updateCommerce(@PathVariable UUID id,
                        @RequestBody UpdateCommerceRequest request);
}
