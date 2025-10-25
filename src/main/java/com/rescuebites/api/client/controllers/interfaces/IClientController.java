package com.rescuebites.api.client.controllers.interfaces;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/v1/clients")
public interface IClientController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    void createClient(@RequestPart("client") @Valid CreateClientRequest createClientRequest,
                      @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture);

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    ClientResponse getClientById(@PathVariable("id") UUID clientId);
}