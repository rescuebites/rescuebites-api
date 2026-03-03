package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.events.EmailUpdatedEvent;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IClientFacade clientFacade;
    private final IUserService userService;
    private final ITokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.default-profile-picture}")
    private String defaultProfilePictureUrl;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {
        User user = userService.findByIdOrThrowException(createClientRequest.getUserId());

        Image image = clientFacade.processProfilePictureForCreation(profilePicture, defaultProfilePictureUrl);
        Client client = ClientMapper.toClient(createClientRequest, user, createClientRequest.getPreferences(), image);
        clientRepository.save(client);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        SecurityUtils.validateOwnership(client.getUser().getEmail());

        return ClientMapper.toClientResponse(client);
    }

    @Override
    @Transactional
    public void updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        User user = client.getUser();
        SecurityUtils.validateOwnership(user.getEmail());

        boolean emailChanged = clientFacade.validateAndProcessUpdate(user, updateClientRequest, profilePicture);
        Image newImage = clientFacade.processProfilePictureForUpdate(client.getImage(), profilePicture);

        // Aplicar cambios
        ClientMapper.updateClientFromRequest(client, updateClientRequest, newImage, updateClientRequest.getPreferences());
        clientFacade.applyUserChanges(user, updateClientRequest, emailChanged);

        client.setUpdatedAt(LocalDateTime.now());
        clientRepository.save(client);

        if (emailChanged) {
            sendEmailChangeConfirmation(user);
        }
    }

    @Override
    @Transactional
    public void deleteClient(UUID clientId) {
        Client client = clientFacade.findClientByIdOrThrowException(clientId);
        User user = client.getUser();
        SecurityUtils.validateOwnership(user.getEmail());

        clientFacade.cleanupClientImage(client.getImage());

        client.setImage(null);
        client.setPreferences(new ArrayList<>());
        client.setDeleted(true);
        client.setDeletedAt(LocalDateTime.now());

        user.setDeleted(true);
        user.setEnabled(false);
        user.setDeletedAt(LocalDateTime.now());

        tokenService.deleteTokensByUser(user);
        clientRepository.save(client);
    }

    private void sendEmailChangeConfirmation(User user) {
        UUID tokenId = tokenService.findLatestTokenByUser(user).getTokenId();
        eventPublisher.publishEvent(new EmailUpdatedEvent(user, tokenId));
    }
}