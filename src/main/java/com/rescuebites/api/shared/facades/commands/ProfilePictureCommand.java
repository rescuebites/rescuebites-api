package com.rescuebites.api.shared.facades.commands;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public record ProfilePictureCommand(
        MultipartFile file,
        boolean required,
        String targetFolder
) {

    private static final String DEFAULT_FOLDER = "uploads/profile-pictures";

    public ProfilePictureCommand {
        Objects.requireNonNull(targetFolder, "targetFolder must not be null");
    }

    public static ProfilePictureCommand required(MultipartFile file) {
        return new ProfilePictureCommand(file, true, DEFAULT_FOLDER);
    }

    public static ProfilePictureCommand optional(MultipartFile file) {
        return new ProfilePictureCommand(file, false, DEFAULT_FOLDER);
    }

    public boolean hasFile() {
        return file != null && !file.isEmpty();
    }
}
