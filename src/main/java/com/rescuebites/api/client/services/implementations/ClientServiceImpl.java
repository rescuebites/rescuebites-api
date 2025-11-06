package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.shared.EmailBuilder;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.commands.ProfilePictureCommand;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.Token;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.commands.PasswordPairCommand;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final IUserFacade userFacade;
    private final IImageFacade imageFacade;
    private final ITokenService tokenService;
    private final EmailBuilder emailBuilder;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {

        User user = userService.findByIdOrThrowException(createClientRequest.userId());
        List<PreferenceType> preferences = resolvePreferences(createClientRequest.preferences());
        Image image = imageFacade.processProfilePicture(ProfilePictureCommand.optional(profilePicture));

        Client client = ClientMapper.toClient(createClientRequest, user, preferences, image);
        clientRepository.save(client);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {

        Client client = findClientByIdOrThrowException(clientId);

        return ClientMapper.toClientResponse(client);
    }

    @Override
    @Transactional
    public void updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {

        Client client = findClientByIdOrThrowException(clientId);

        User user = client.getUser();
        Image newImage = imageFacade.replaceProfilePicture(
                client.getImage(),
                ProfilePictureCommand.optional(profilePicture)
        );
        List<PreferenceType> preferences = resolvePreferences(updateClientRequest.preferences());
        ClientMapper.updateClientFromRequest(client, updateClientRequest, newImage, preferences);

        boolean emailChanged = updateEmailIfChanged(user, updateClientRequest.email());

        updatePasswordIfProvided(user, updateClientRequest.password(), updateClientRequest.confirmPassword());

        user.setEnabled(false);
        clientRepository.save(client);

        if (emailChanged) {
            sendEmailChangeConfirmation(client, user);
        }
    }

    private void sendEmailChangeConfirmation(Client client, User user) {

        Token token = tokenService.findLatestTokenByUser(user);

        String emailBody = emailBuilder.buildEmailUpdatedConfirmation(client.getFullName(), user, token.getTokenId());
        emailService.sendEmail(user.getEmail(), "Email actualizado ✔", emailBody);
    }

    private boolean updateEmailIfChanged(User user, String newEmail) {
        if (!StringUtils.hasText(newEmail)) {
            return false;
        }

        String trimmedEmail = newEmail.trim();
        if (trimmedEmail.equals(user.getEmail())) {
            return false;
        }

        userFacade.ensureEmailIsAvailable(trimmedEmail);
        user.setEmail(trimmedEmail);
        return true;
    }

    private void updatePasswordIfProvided(User user, String newPassword, String confirmNewPassword) {
        if (!StringUtils.hasText(newPassword) && !StringUtils.hasText(confirmNewPassword)) {
            return;
        }

        userFacade.ensurePasswordsMatch(new PasswordPairCommand(newPassword, confirmNewPassword));
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private List<PreferenceType> resolvePreferences(List<PreferenceType> preferences) {
        if (preferences == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(preferences);
    }

    private Client findClientByIdOrThrowException(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

}