package com.rescuebites.api.client.facades.interfaces;

import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface IClientFacade {

    Image processProfilePictureForCreation(MultipartFile profilePicture, String defaultProfilePictureUrl);

    Image processProfilePictureForUpdate(Image currentImage, MultipartFile newProfilePicture);

    void cleanupClientImage(Image image);

    boolean validateAndProcessUpdate(User user, UpdateClientRequest request, MultipartFile profilePicture);

    void applyUserChanges(User user, UpdateClientRequest request, boolean emailChanged);
}