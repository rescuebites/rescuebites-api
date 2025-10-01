package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceProfileResponse;
import com.rescuebites.api.commerce.data.mappers.CommercesMapper;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.repositories.ICommerceTypeRepository;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.ImageService;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rescuebites.api.client.utils.Constants.MAXIMUM_FILE_SIZE;

@Service
@RequiredArgsConstructor
public class commerceService implements ICommerceService {
    private final ICommerceRepository commerceRepository;
    private final IUserService userService;
    private final ImageService imageService;
    private final ICommerceTypeRepository commerceTypeRepository;

    //Register Commerce
    @Override
    @Transactional
    public void registerCommerce(RegisterCommerceRequest registerCommerceRequest, MultipartFile profilePhoto){
        validateProfilePicture(profilePhoto);

        User user = userService.findByIdOrThrowException(registerCommerceRequest.userId());
        List<CommerceType> commerceTypes = loadCommerceTypes(registerCommerceRequest);
        Image image = imageService.uploadAndSaveImage(profilePhoto);

        Commerce commerce = CommercesMapper.toCommerce(registerCommerceRequest, commerceTypes, user, image);
        commerceRepository.save(commerce);
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

    private List<CommerceType> loadCommerceTypes(RegisterCommerceRequest registerCommerceRequest){
        if (registerCommerceRequest.commerceTypes() != null && !registerCommerceRequest.commerceTypes().isEmpty()){
            return commerceTypeRepository.findAllById(registerCommerceRequest.commerceTypes());
        }
        return new ArrayList<>();
    }


    //Update Commerce
    @Override
    public void updateCommerce(UUID id, UpdateCommerceRequest request) {
        // Obtenemos el comercio (en teoría ya existe porque viene del perfil)
        Commerce existingCommerce = commerceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("commerce", "id", id));
        boolean modified = false;

        // Validaciones campo por campo, si el nombre es distinto, se actualiza
        if (request.name() != null && !request.name().isBlank()
                && !request.name().equals(existingCommerce.getName())) {
            existingCommerce.setName(request.name());
            modified = true;
        }

        if (request.description() != null && !request.description().isBlank()
                && !request.description().equals(existingCommerce.getDescription())) {
            existingCommerce.setDescription(request.description());
            modified = true;
        }

        if (request.commerceTypes() != null
                && !request.commerceTypes().equals(existingCommerce.getCommerceTypes())) {
            existingCommerce.setCommerceTypes(request.commerceTypes());
            modified = true;
        }

        if (request.openingHours() != null
                && !request.openingHours().equals(existingCommerce.getOpeningHours())) {
            existingCommerce.setOpeningHours(request.openingHours());
            modified = true;
        }

        if (request.direction() != null
                && !request.direction().equals(existingCommerce.getDirection())) {
            existingCommerce.setDirection(request.direction());
            modified = true;
        }

        if (request.phone() != null
                && !request.phone().equals(existingCommerce.getPhone())) {
            existingCommerce.setPhone(request.phone());
            modified = true;
        }

        if (request.image() != null
                && !request.image().equals(existingCommerce.getImage())) {
            existingCommerce.setImage(request.image());
            modified = true;
        }

        //Si no se modificó nada, lanza una excepción
        if (!modified) {
            throw new RuntimeException("Debe modificar al menos un campo");
        }

        //Guarda los cambios en la base de datos
        commerceRepository.save(existingCommerce);
    }

    //Commerce Profile
//    @Override
//    public CommerceProfileResponse getCommerceProfile(UUID id) {
//        Commerce commerce = commerceRepository.findById(id) //Busca el comercio (id) en la base de datos
//                .orElseThrow(() -> new ResourceNotFoundException("commerce", "id", id)); //Tira excepcion si no existe
//        return CommercesMapper.toCommerceProfileResponse(commerce);
//
//    }
}
