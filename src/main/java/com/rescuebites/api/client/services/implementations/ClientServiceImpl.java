package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.controllers.responses.ClientResponse;
import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.exceptions.custom_exceptions.DuplicateResourceException;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.shared.EmailBuilder;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.ImageService;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,22}$");
    private static final String PASSWORD_REQUIREMENTS_MESSAGE = "La contraseña debe tener entre 8 y 22 caracteres, e incluir al menos una mayúscula, una minúscula, un número y un caracter especial";

    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final ImageService imageService;
    private final IEmailService emailService;
    private final EmailBuilder emailBuilder;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {

        validateProfilePicture(profilePicture);

        User user = userService.findByIdOrThrowException(createClientRequest.userId());
        List<PreferenceType> preferences = resolvePreferences(createClientRequest.preferences());
        Image image = imageService.uploadAndSaveImage(profilePicture);

        Client client = ClientMapper.toClient(createClientRequest, user, preferences, image);
        clientRepository.save(client);
    }

    @Override
    public ClientResponse getClientById(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        return ClientMapper.toClientResponse(client);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(UUID clientId, UpdateClientRequest updateClientRequest, MultipartFile profilePicture) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        updateClientBasicInformation(client, updateClientRequest);
        updateClientProfilePictureIfNeeded(client, profilePicture);

        User user = client.getUser();

        boolean emailChanged = updateEmailIfChanged(user, updateClientRequest.email());
        updatePasswordIfProvided(user, updateClientRequest.newPassword(), updateClientRequest.confirmNewPassword());

        Client savedClient = clientRepository.save(client);

        if (emailChanged) {
            sendEmailChangeConfirmation(client, user);
        }

        return ClientMapper.toClientResponse(savedClient);
    }

    private void updateClientBasicInformation(Client client, UpdateClientRequest updateClientRequest) {
        client.setFirstName(updateClientRequest.firstName());
        client.setLastName(updateClientRequest.lastName());
        client.setBirthDate(updateClientRequest.birthDate());
        client.setAddress(updateClientRequest.address());
        client.setPreferences(resolvePreferences(updateClientRequest.preferences()));
    }

    private void updateClientProfilePictureIfNeeded(Client client, MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return;
        }

        ifProfilePictureIsNotJpgOrPngThrowException(profilePicture.getContentType());
        ifProfilePictureExceedsMaximumSizeThrowException(profilePicture);

        Image currentImage = client.getImage();
        if (currentImage != null && StringUtils.hasText(currentImage.getPublicId())) {
            imageService.deleteImage(currentImage.getPublicId());
        }

        Image newImage = imageService.uploadAndSaveImage(profilePicture);
        client.setImage(newImage);
    }

    private boolean updateEmailIfChanged(User user, String newEmail) {
        if (!StringUtils.hasText(newEmail)) {
            return false;
        }

        String trimmedEmail = newEmail.trim();
        String currentEmail = user.getEmail();

        if (trimmedEmail.equalsIgnoreCase(currentEmail)) {
            return false;
        }

        if (userService.emailExists(trimmedEmail)) {
            throw new DuplicateResourceException("User", "email");
        }

        user.setEmail(trimmedEmail);
        return true;
    }

    private void updatePasswordIfProvided(User user, String newPassword, String confirmNewPassword) {
        boolean hasNewPassword = StringUtils.hasText(newPassword);
        boolean hasConfirmPassword = StringUtils.hasText(confirmNewPassword);

        if (!hasNewPassword && !hasConfirmPassword) {
            return;
        }

        if (!hasNewPassword || !hasConfirmPassword) {
            throw new IllegalArgumentException("Debe ingresar y confirmar la nueva contraseña");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (!STRONG_PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new IllegalArgumentException(PASSWORD_REQUIREMENTS_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    private void sendEmailChangeConfirmation(Client client, User user) {
        String emailBody = emailBuilder.buildEmailUpdatedConfirmation(client.getFullName(), user.getEmail());
        emailService.sendEmail(user.getEmail(), "Email actualizado ✔", emailBody);
    }

    private void validateProfilePicture(MultipartFile profilePicture) {
        ifProfilePictureIsMissingThrowException(profilePicture);

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

    private List<PreferenceType> resolvePreferences(List<PreferenceType> preferences) {
        if (preferences == null) {
            return List.of();
        }
        return List.copyOf(preferences);
    }
}