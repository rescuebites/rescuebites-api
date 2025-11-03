package com.rescuebites.api.client.services.interfaces;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IClientService {
    void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture);

    ClientResponse getClientById(UUID clientId);

    ClientResponse updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture);
}
