package com.rescuebites.api.client.data.mappers;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.client.data.mappers.ImageMapper.toImageResponse;
import static com.rescuebites.api.users.data.mappers.UserMapper.toUserResponse;

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
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phone(request.getPhone())
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
                toImageResponse(client.getImage()),
                client.getAddress(),
                client.getPhone(),
                toUserResponse(client.getUser()),
                client.getPreferences()
        );
    }

    public static void updateClientFromRequest(
            Client client,
            UpdateClientRequest request,
            Image newImage,
            List<PreferenceType> preferences
    ) {
        if (request.getFirstName() != null) {
            client.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            client.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            client.setBirthDate(request.getBirthDate());
        }
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        if(request.getPhone() != null){
            client.setPhone(request.getPhone());
        }
        if (preferences != null && !preferences.isEmpty()) {
            client.setPreferences(preferences);
        }
        if (newImage != null) {
            client.setImage(newImage);
        }
    }
}