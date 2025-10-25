package com.rescuebites.api.client.data.mappers;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public static Client toClient(
            CreateClientRequest request,
            User user,
            List<PreferenceType> preferences,
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

    public static ClientResponse toClientResponse(Client client) {
        return new ClientResponse(
                client.getClientId(),
                client.getFirstName(),
                client.getLastName(),
                client.getBirthDate(),
                client.getImage(),
                client.getAddress(),
                client.getUser(),
                client.getPreferences().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
    }
}