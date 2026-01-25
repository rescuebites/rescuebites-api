package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.events.EmailUpdatedEvent;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommerceServiceImpl implements ICommerceService {

    private final ICommerceRepository commerceRepository;
    private final IUserService userService;
    private final ICommerceFacade commerceFacade;
    private final IImageFacade imageFacade;
    private final ITokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createCommerce(CreateCommerceRequest createCommerceRequest, MultipartFile[] images) {
        User user = userService.findByIdOrThrowException(createCommerceRequest.getUserId());
        SecurityUtils.validateOwnership(user.getEmail());

        commerceFacade.ifCommerceNameAlreadyExistsThrowException(createCommerceRequest.getName());

        imageFacade.validateImages(images);
        List<Image> uploadedImages = imageFacade.uploadAndSaveImages(images);
        List<CommerceType> commerceTypes = commerceFacade.getOrCreateCommerceTypes(createCommerceRequest.getCommerceTypes());

        Commerce commerce = CommerceMapper.toCommerce(createCommerceRequest, user, commerceTypes, uploadedImages);
        commerceRepository.save(commerce);
    }

    @Override
    public CommerceResponse getCommerceById(UUID commerceId) {
        Commerce commerce = findCommerceByIdOrThrowException(commerceId);
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());

        return CommerceMapper.toCommerceResponse(commerce);
    }

    @Override
    @Transactional
    public void updateCommerce(UUID commerceId, UpdateCommerceRequest updateCommerceRequest, MultipartFile[] images) {
        Commerce commerce = findCommerceByIdOrThrowException(commerceId);
        User user = commerce.getUser();
        SecurityUtils.validateOwnership(user.getEmail());

        boolean emailChanged = commerceFacade.validateAndProcessUpdate(user, updateCommerceRequest);

        List<Image> newImages = processImagesIfProvided(commerce.getImages(), images);

        List<CommerceType> commerceTypes = updateCommerceRequest.getCommerceTypes() != null && !updateCommerceRequest.getCommerceTypes().isEmpty()
                ? commerceFacade.getOrCreateCommerceTypes(updateCommerceRequest.getCommerceTypes())
                : null;

        CommerceMapper.updateCommerceFromRequest(commerce, updateCommerceRequest, commerceTypes, newImages);

        commerceFacade.applyUserChanges(user, updateCommerceRequest, emailChanged);
        commerce.setUpdatedAt(LocalDateTime.now());
        commerceRepository.save(commerce);

        if (emailChanged) {
            sendEmailChangeConfirmation(user);
        }
    }

    @Override
    @Transactional
    public void deleteCommerce(UUID commerceId) {
        Commerce commerce = findCommerceByIdOrThrowException(commerceId);
        User user = commerce.getUser();
        SecurityUtils.validateOwnership(user.getEmail());
        List<Image> currentImages = commerce.getImages();

        if (currentImages != null && !currentImages.isEmpty()) {
            currentImages.stream()
                    .filter(image -> StringUtils.hasText(image.getPublicId()))
                    .forEach(image -> imageFacade.deleteImage(image.getPublicId()));
        }

        tokenService.deleteTokensByUser(user);

        commerce.getImages().clear();
        commerce.getCommerceTypes().clear();
        commerce.setDeleted(true);
        commerce.setDeletedAt(LocalDateTime.now());

        user.setDeleted(true);
        user.setEnabled(false);
        user.setDeletedAt(LocalDateTime.now());

        commerceRepository.save(commerce);
    }

    private List<Image> processImagesIfProvided(List<Image> currentImages, MultipartFile[] newImages) {
        if (newImages == null || newImages.length == 0) {
            return currentImages;
        }

        return imageFacade.processAndUpdateImages(currentImages, newImages);
    }

    private void sendEmailChangeConfirmation(User user) {
        UUID tokenId = tokenService.findLatestTokenByUser(user).getTokenId();
        eventPublisher.publishEvent(new EmailUpdatedEvent(user, tokenId));
    }

    private Commerce findCommerceByIdOrThrowException(UUID commerceId) {
        return commerceRepository.findByCommerceIdAndDeletedFalse(commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Commerce", "id", commerceId));
    }
}