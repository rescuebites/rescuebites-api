package com.rescuebites.api.client.data.mappers;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.data.models.Preference;
import com.rescuebites.api.data.models.Image;
import com.rescuebites.api.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ClientMapper {

    // DTO -> Entidad
    public static Client toClient(
            CreateClientRequest request,
            User user,
            List<Preference> preferences,
            Image image
    ) {
        return Client.builder()
                .clientId(UUID.randomUUID())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .address(request.address())
                .user(user)
                .preferences(preferences)
                .image(image)
                .build();
    }
}