package com.rescuebites.api.client.controllers.interfaces;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/api/v1/clients")
public interface IClientController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    void createClient(@RequestPart("client") @Valid CreateClientRequest createClientRequest,
                                      @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture);

    @GetMapping("/{clientId}")
    @ResponseStatus(OK)
    ClientResponse getClientById(@PathVariable UUID clientId);

    @PatchMapping(value = "/{clientId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(OK)
    void updateClient(@PathVariable("clientId") UUID clientId,
                                @RequestPart("client") @Valid UpdateClientRequest updateClientRequest,
                                @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture);

    @DeleteMapping("/{clientId}")
    @ResponseStatus(NO_CONTENT)
    void deleteClient(@PathVariable UUID clientId);
}