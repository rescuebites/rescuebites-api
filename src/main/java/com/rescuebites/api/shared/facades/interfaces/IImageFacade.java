package com.rescuebites.api.shared.facades.interfaces;

import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.commands.ProfilePictureCommand;

public interface IImageFacade {

    Image processProfilePicture(ProfilePictureCommand command);

    Image replaceProfilePicture(Image currentImage, ProfilePictureCommand command);

    void removeImage(String publicId);
}
