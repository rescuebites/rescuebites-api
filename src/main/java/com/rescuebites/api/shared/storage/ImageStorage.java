package com.rescuebites.api.shared.storage;

import com.rescuebites.api.shared.storage.models.StoredImage;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    StoredImage store(MultipartFile file, String folder);

    void delete(String publicId);
}
