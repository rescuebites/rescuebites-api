package com.rescuebites.api.client.services.implementations;

import com.rescuebites.api.client.controllers.requests.CreateClientRequest;
import com.rescuebites.api.client.data.mappers.ClientMapper;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.data.models.Preference;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.repositories.IPreferenceRepository;
import com.rescuebites.api.client.services.interfaces.IClientService;
import com.rescuebites.api.data.models.Image;
import com.rescuebites.api.data.models.User;
import com.rescuebites.api.services.implementations.ImageService;
import com.rescuebites.api.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.rescuebites.api.client.Constants.MAXIMUM_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IPreferenceRepository preferenceRepository;
    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final ImageService imageService;

    @Override
    @Transactional
    public void createClient(CreateClientRequest createClientRequest, MultipartFile profilePicture) {

        validateProfilePicture(profilePicture);

        User user = userService.findByIdOrThrowException(createClientRequest.userId());
        List<Preference> preferences = loadPreferencesIfProvided(createClientRequest);
        Image image = imageService.uploadAndSaveImage(profilePicture);

        Client client = ClientMapper.toClient(createClientRequest, user, preferences, image);
        clientRepository.save(client);
    }

    private void validateProfilePicture(MultipartFile profilePicture) {
        //ifProfilePictureIsMissingThrowException(profilePicture);

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

//    private void ifProfilePictureIsMissingThrowException(MultipartFile profilePicture) {
//        if (profilePicture == null) {
//            throw new IllegalArgumentException("Debe registrar una foto de perfil");
//        }
//    }

    private List<Preference> loadPreferencesIfProvided(CreateClientRequest createClientRequest) {
        if (createClientRequest.preferenceIds() != null && !createClientRequest.preferenceIds().isEmpty()) {
            return preferenceRepository.findAllById(createClientRequest.preferenceIds());
        }
        return new ArrayList<>();
    }
}