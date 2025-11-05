package com.rescuebites.api.client.controllers.implementations;

import com.rescuebites.api.client.controllers.interfaces.IClientController;
import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.services.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientControllerImpl implements IClientController {

    private final IClientService clientService;

    @Override
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {
        clientService.createClient(createClientRequest, profilePicture);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {
        return clientService.getClientById(clientId);
    }

    @Override
    public void updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {
        clientService.updateClient(clientId, updateClientRequest, profilePicture);
    }
}
