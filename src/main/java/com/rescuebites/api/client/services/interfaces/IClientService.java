package com.rescuebites.api.client.services.interfaces;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IClientService {
    void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture);
}
