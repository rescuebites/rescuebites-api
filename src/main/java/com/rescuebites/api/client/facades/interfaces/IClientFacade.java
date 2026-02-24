package com.rescuebites.api.client.facades.interfaces;

import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IClientFacade {

    Client findClientByIdOrThrowException(UUID clientId);

    Image processProfilePictureForCreation(MultipartFile profilePicture, String defaultProfilePictureUrl);

    Image processProfilePictureForUpdate(Image currentImage, MultipartFile newProfilePicture);

    void cleanupClientImage(Image image);

    boolean validateAndProcessUpdate(User user, UpdateClientRequest request, MultipartFile profilePicture);

    void applyUserChanges(User user, UpdateClientRequest request, boolean emailChanged);
}