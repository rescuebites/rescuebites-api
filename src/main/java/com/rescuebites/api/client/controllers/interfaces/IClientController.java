package com.rescuebites.api.client.controllers.interfaces;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/api/v1/clients")
public interface IClientController {

    @PostMapping
    @ResponseStatus(CREATED)
    void createClient(@RequestPart("client") @Valid CreateClientRequest createClientRequest,
                      @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture);
}