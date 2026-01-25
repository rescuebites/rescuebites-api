package com.rescuebites.api.client.facades.implementations;

import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.facades.interfaces.IClientFacade;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClientFacade implements IClientFacade {

    private final IUserFacade userFacade;
    private final IImageFacade imageFacade;
    private final ITokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Image processProfilePictureForCreation(MultipartFile profilePicture, String defaultProfilePictureUrl) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            return createDefaultImage(defaultProfilePictureUrl);
        }

        imageFacade.ifProfilePictureIsNotJpgOrPngThrowException(profilePicture.getContentType());
        imageFacade.ifProfilePictureExceedsMaximumSizeThrowException(profilePicture);

        return imageFacade.uploadAndSaveImage(profilePicture);
    }

    @Override
    public Image processProfilePictureForUpdate(Image currentImage, MultipartFile newProfilePicture) {
        if (newProfilePicture == null || newProfilePicture.isEmpty()) {
            return currentImage;
        }

        // Si tiene imagen default (sin publicId), solo sube la nueva
        if (currentImage != null && !StringUtils.hasText(currentImage.getPublicId())) {
            imageFacade.ifProfilePictureIsNotJpgOrPngThrowException(newProfilePicture.getContentType());
            imageFacade.ifProfilePictureExceedsMaximumSizeThrowException(newProfilePicture);
            return imageFacade.uploadAndSaveImage(newProfilePicture);
        }

        // Si tiene imagen real, uso replaceImage (valida, sube nueva, borra anterior)
        return imageFacade.replaceImage(currentImage, newProfilePicture);
    }

    @Override
    public void cleanupClientImage(Image image) {
        if (image != null && StringUtils.hasText(image.getPublicId())) {
            imageFacade.deleteImage(image.getPublicId());
        }
    }

    @Override
    public boolean validateAndProcessUpdate(User user, UpdateClientRequest request, MultipartFile profilePicture) {
        validateAtLeastOneFieldToUpdate(request, profilePicture);

        boolean emailChanged = userFacade.validateAndCheckEmailChange(user, request.getEmail());
        userFacade.validatePasswordsIfProvided(request.getPassword(), request.getConfirmPassword());

        return emailChanged;
    }

    @Override
    public void applyUserChanges(User user, UpdateClientRequest request, boolean emailChanged) {
        if (emailChanged) {
            user.setEmail(request.getEmail().trim());
            user.setEnabled(false);
            tokenService.saveUserToken(user);
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private void validateAtLeastOneFieldToUpdate(UpdateClientRequest request, MultipartFile profilePicture) {
        boolean hasChanges = StringUtils.hasText(request.getFirstName()) ||
                StringUtils.hasText(request.getLastName()) ||
                request.getBirthDate() != null ||
                StringUtils.hasText(request.getAddress()) ||
                StringUtils.hasText(request.getEmail()) ||
                StringUtils.hasText(request.getPassword()) ||
                (request.getPreferences() != null && !request.getPreferences().isEmpty()) ||
                (profilePicture != null && !profilePicture.isEmpty());

        if (!hasChanges) {
            throw new ValidationException("Debe modificar al menos un campo");
        }
    }

    private Image createDefaultImage(String defaultProfilePictureUrl) {
        Image defaultImage = new Image();
        defaultImage.setUrl(defaultProfilePictureUrl);
        defaultImage.setPublicId(null);
        return defaultImage;
    }
}