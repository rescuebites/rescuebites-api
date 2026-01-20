package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.exceptions.custom_exceptions.PasswordsDoNotMatchException;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final IUserFacade userFacade;
    private final IImageFacade imageFacade;
    private final ITokenService tokenService;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-profile-picture}")
    private String defaultProfilePictureUrl;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {
        User user = userService.findByIdOrThrowException(createClientRequest.getUserId());
        Image image = getProfilePictureOrDefault(profilePicture);

        Client client = ClientMapper.toClient(createClientRequest, user, createClientRequest.getPreferences(), image);
        clientRepository.save(client);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {
        return clientRepository.findByClientIdAndDeletedFalse(clientId)
                .map(ClientMapper::toClientResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

    @Override
    @Transactional
    public void updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {
        Client client = findClientByIdOrThrowException(clientId);
        User user = client.getUser();

        Image newImage = processProfilePictureIfProvided(client.getImage(), profilePicture);
        ClientMapper.updateClientFromRequest(client, updateClientRequest, newImage, updateClientRequest.getPreferences());

        boolean emailChanged = updateEmailIfChanged(user, updateClientRequest.getEmail());
        updatePasswordIfProvided(user, updateClientRequest.getPassword(), updateClientRequest.getConfirmPassword());

        user.setEnabled(false);
        clientRepository.save(client);

        if (emailChanged) {
            sendEmailChangeConfirmation(client, user);
        }
    }

    @Override
    @Transactional
    public void deleteClient(UUID clientId) {
        Client client = findClientByIdOrThrowException(clientId);
        User user = client.getUser();
        Image currentImage = client.getImage();

        if (currentImage != null && StringUtils.hasText(currentImage.getPublicId())) {
            imageFacade.deleteImage(currentImage.getPublicId());
        }

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

    private Image getProfilePictureOrDefault(MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return createDefaultProfilePicture();
        }

        validateProfilePictureIfProvided(profilePicture);
        return imageFacade.uploadAndSaveImage(profilePicture);
    }

    private Image createDefaultProfilePicture() {
        Image defaultImage = new Image();
        defaultImage.setUrl(defaultProfilePictureUrl);
        defaultImage.setPublicId(null);
        return defaultImage;
    }

    private Image processProfilePictureIfProvided(Image currentImage, MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return currentImage;
        }

        validateProfilePictureIfProvided(profilePicture);

        if (currentImage != null && StringUtils.hasText(currentImage.getPublicId())) {
            imageFacade.deleteImage(currentImage.getPublicId());
        }

        return imageFacade.uploadAndSaveImage(profilePicture);
    }

    private void sendEmailChangeConfirmation(Client client, User user) {
        Token token = tokenService.findLatestTokenByUser(user);
        emailService.sendEmailUpdatedConfirmationEmail(user.getEmail(), client.getFullName(), user, token.getTokenId());
    }

    private boolean updateEmailIfChanged(User user, String newEmail) {
        if (!StringUtils.hasText(newEmail)) {
            return false;
        }

        String trimmedEmail = newEmail.trim();
        if (trimmedEmail.equals(user.getEmail())) {
            return false;
        }

        userFacade.ifEmailAlreadyExistsThrowException(trimmedEmail);
        user.setEmail(trimmedEmail);
        return true;
    }

    private void updatePasswordIfProvided(User user, String newPassword, String confirmNewPassword) {
        if (!StringUtils.hasText(newPassword) && !StringUtils.hasText(confirmNewPassword)) {
            return;
        }

        if (!StringUtils.hasText(newPassword) || !StringUtils.hasText(confirmNewPassword)) {
            throw new ValidationException("Debe ingresar y confirmar la nueva contraseña");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private Client findClientByIdOrThrowException(UUID clientId) {
        return clientRepository.findByClientIdAndDeletedFalse(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

    private void validateProfilePictureIfProvided(MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return;
        }
        imageFacade.ifProfilePictureIsNotJpgOrPngThrowException(profilePicture.getContentType());
        imageFacade.ifProfilePictureExceedsMaximumSizeThrowException(profilePicture);
    }
}