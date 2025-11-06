package com.rescuebites.api.shared.validation;

import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.facades.commands.ProfilePictureCommand;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;

@Component
public class ImageValidator {

    private static final Set<String> SUPPORTED_CONTENT_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
    );

    public boolean shouldSkip(ProfilePictureCommand command) {
        return !command.required() && !command.hasFile();
    }

    public void validate(ProfilePictureCommand command) {
        if (shouldSkip(command)) {
            return;
        }

        MultipartFile file = command.file();
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Debe cargar una foto de perfil");
        }

        if (!SUPPORTED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new ValidationException("La foto debe estar en formato JPG o PNG");
        }

        if (file.getSize() > MAXIMUM_FILE_SIZE) {
            throw new ValidationException("La foto no debe superar los 2MB");
        }
    }
}
