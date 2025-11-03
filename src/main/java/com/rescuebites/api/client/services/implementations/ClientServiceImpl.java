package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.ImageService;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.services.interfaces.IUserService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final ImageService imageService;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {

        validateProfilePicture(profilePicture);

        User user = userService.findByIdOrThrowException(createClientRequest.userId());
        List<PreferenceType> preferences = loadPreferencesIfProvided(createClientRequest);
        Image image = imageService.uploadAndSaveImage(profilePicture);

        Client client = ClientMapper.toClient(createClientRequest, user, preferences, image);
        clientRepository.save(client);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {
        Client client = findClientByIdOrThrow(clientId);

        return ClientMapper.toClientResponse(client);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {
        Client client = findClientByIdOrThrow(clientId);

        updateBasicInformation(client, updateClientRequest);
        updateClientUserIfRequired(client, updateClientRequest);
        updateClientPreferences(client, updateClientRequest);
        updateProfilePictureIfProvided(client, profilePicture);

        clientRepository.save(client);

        return ClientMapper.toClientResponse(client);
    }

    private Client findClientByIdOrThrow(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

    private void validateProfilePicture(MultipartFile profilePicture) {
        ifProfilePictureIsMissingThrowException(profilePicture);

        ifProfilePictureIsNotJpgOrPngThrowException(profilePicture.getContentType());

        ifProfilePictureExceedsMaximumSizeThrowException(profilePicture);
    }

    private void validateProfilePictureIfProvided(MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return;
        }

        ifProfilePictureIsNotJpgOrPngThrowException(profilePicture.getContentType());
        ifProfilePictureExceedsMaximumSizeThrowException(profilePicture);
    }

    private void ifProfilePictureExceedsMaximumSizeThrowException(MultipartFile file) {
        if (file.getSize() > MAXIMUM_FILE_SIZE) {
            throw new IllegalArgumentException("La foto de perfil no debe superar los 2MB");
        }
    }

    private void ifProfilePictureIsNotJpgOrPngThrowException(String contentType) {
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IllegalArgumentException("La foto de perfil debe estar en formato JPG o PNG");
        }
    }

    private void ifProfilePictureIsMissingThrowException(MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar una foto de perfil");
        }
    }

    private List<PreferenceType> loadPreferencesIfProvided(CreateClientRequest createClientRequest) {
        if (createClientRequest.preferences() != null) {
            return createClientRequest.preferences();
        }
        return List.of();
    }

    private void updateBasicInformation(Client client, UpdateClientRequest updateClientRequest) {
        if (updateClientRequest.firstName() != null) {
            client.setFirstName(validateAndSanitize(updateClientRequest.firstName(), "El nombre es obligatorio"));
        }

        if (updateClientRequest.lastName() != null) {
            client.setLastName(validateAndSanitize(updateClientRequest.lastName(), "El apellido es obligatorio"));
        }

        if (updateClientRequest.birthDate() != null) {
            validateBirthDate(updateClientRequest.birthDate());
            client.setBirthDate(updateClientRequest.birthDate());
        }

        if (updateClientRequest.address() != null) {
            client.setAddress(validateAndSanitize(updateClientRequest.address(), "La dirección es obligatoria"));
        }
    }

    private void updateClientUserIfRequired(Client client, UpdateClientRequest updateClientRequest) {
        if (updateClientRequest.userId() == null) {
            return;
        }

        UUID currentUserId = client.getUser() != null ? client.getUser().getUserId() : null;
        if (!updateClientRequest.userId().equals(currentUserId)) {
            User user = userService.findByIdOrThrowException(updateClientRequest.userId());
            client.setUser(user);
        }
    }

    private void updateClientPreferences(Client client, UpdateClientRequest updateClientRequest) {
        if (updateClientRequest.preferences() != null) {
            client.setPreferences(updateClientRequest.preferences());
        }
    }

    private void updateProfilePictureIfProvided(Client client, MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return;
        }

        validateProfilePictureIfProvided(profilePicture);

        Image currentImage = client.getImage();
        Image newImage = imageService.uploadAndSaveImage(profilePicture);
        client.setImage(newImage);

        if (currentImage != null && currentImage.getPublicId() != null) {
            imageService.deleteImage(currentImage.getPublicId());
        }
    }

    private String validateAndSanitize(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new ValidationException(message);
        }

        return value.trim();
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ValidationException("La fecha de nacimiento no puede ser futura");
        }
    }
}