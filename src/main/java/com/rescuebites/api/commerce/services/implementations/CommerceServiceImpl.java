package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceCredentialsRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.ICommerceService;
import com.rescuebites.api.location.data.models.Locality;
import com.rescuebites.api.location.services.implementations.LocalityService;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.events.EmailUpdatedEvent;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final LocalityService localityService;

    @Override
    @Transactional
    public void createCommerce(CreateCommerceRequest createCommerceRequest, MultipartFile[] images) {
        User user = userService.findByIdOrThrowException(createCommerceRequest.getUserId());

        Locality locality = localityService.resolveOrCreateByName(createCommerceRequest.getLocality());

        commerceFacade.ifCommerceIdentityAlreadyExistsThrowException(
                createCommerceRequest.getName(),
                createCommerceRequest.getAddress(),
                locality.getName()
        );
        commerceFacade.validateBusinessHours(createCommerceRequest.getBusinessHours(), true);

        imageFacade.validateImages(images);
        List<Image> storedProduct = imageFacade.uploadAndSaveImages(images);
        List<CommerceType> commerceTypes = commerceFacade.getOrCreateCommerceTypes(createCommerceRequest.getCommerceTypes());

        Commerce commerce = CommerceMapper.toCommerce(
                createCommerceRequest,
                user,
                commerceTypes,
                storedProduct,
                locality
        );
        commerceRepository.save(commerce);
    }

    @Override
    @Transactional
    public void updateCommerce(UUID commerceId, UpdateCommerceRequest updateCommerceRequest, MultipartFile[] images) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);
        User user = commerce.getUser();
        SecurityUtils.validateOwnership(user.getEmail());

        boolean emailChanged = commerceFacade.validateAndProcessUpdate(
                user,
                updateCommerceRequest
        );

        Locality locality = resolveLocalityIfNeeded(updateCommerceRequest);

        String effectiveName = updateCommerceRequest.getName() != null
                ? updateCommerceRequest.getName()
                : commerce.getName();

        String effectiveAddress = updateCommerceRequest.getAddress() != null
                ? updateCommerceRequest.getAddress()
                : commerce.getAddress();

        String effectiveLocalityName = locality != null
                ? locality.getName()
                : commerce.getLocality() != null
                ? commerce.getLocality().getName()
                : null;

        commerceFacade.ifCommerceIdentityAlreadyExistsThrowExceptionExcludingId(
                commerceId,
                effectiveName,
                effectiveAddress,
                effectiveLocalityName
        );

        // Procesar imágenes si se enviaron nuevas
        // addImagesToExisting agrega directamente a commerce.getImages()
        if (images != null && images.length > 0) {
            int sizeBeforeAdd = commerce.getImages().size();
            imageFacade.addImagesToExisting(commerce.getImages(), images);

            // Establecer la relación bidireccional para las nuevas imágenes
            for (int i = sizeBeforeAdd; i < commerce.getImages().size(); i++) {
                commerce.getImages().get(i).setCommerce(commerce);
            }
        }

        List<CommerceType> commerceTypes = updateCommerceRequest.getCommerceTypes() != null
                && !updateCommerceRequest.getCommerceTypes().isEmpty()
                ? commerceFacade.getOrCreateCommerceTypes(updateCommerceRequest.getCommerceTypes())
                : null;

        // El mapper NO debe recibir imágenes - ya están en commerce.getImages()
        CommerceMapper.updateCommerceFromRequest(
                commerce,
                updateCommerceRequest,
                commerceTypes,
                null, // null porque las imágenes ya fueron agregadas directamente
                locality
        );
        commerceFacade.applyUserChanges(
                user,
                updateCommerceRequest,
                emailChanged
        );

        commerce.setUpdatedAt(LocalDateTime.now());
        commerceRepository.save(commerce);

        if (emailChanged) {
            sendEmailChangeConfirmation(user);
        }
    }

    @Override
    @Transactional
    public void deleteCommerce(UUID commerceId) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);
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

    @Override
    @Transactional
    public void updateCommerceCredentials(UUID commerceId, UpdateCommerceCredentialsRequest request) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());

        commerce.setMercadoPagoAccessToken(request.getMercadoPagoAccessToken());
        commerce.setMercadoPagoWebhookSecret(request.getMercadoPagoWebhookSecret());
        commerce.setUpdatedAt(LocalDateTime.now());

        commerceRepository.save(commerce);
    }

    private void sendEmailChangeConfirmation(User user) {
        UUID tokenId = tokenService.findLatestTokenByUser(user).getTokenId();
        eventPublisher.publishEvent(new EmailUpdatedEvent(user, tokenId));
    }

    private Locality resolveLocalityIfNeeded(UpdateCommerceRequest request) {
        if (!StringUtils.hasText(request.getLocality())) {
            return null;
        }

        return localityService.resolveOrCreateByName(request.getLocality());
    }
}

