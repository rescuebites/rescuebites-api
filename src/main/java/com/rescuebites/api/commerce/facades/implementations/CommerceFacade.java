package com.rescuebites.api.commerce.facades.implementations;

import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.repositories.ICommerceTypeRepository;
import com.rescuebites.api.exceptions.custom_exceptions.DuplicateResourceException;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.security.utils.SecurityUtils;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommerceFacade implements ICommerceFacade {

    private final ICommerceRepository commerceRepository;
    private final ICommerceTypeRepository commerceTypeRepository;
    private final IUserFacade userFacade;
    private final ITokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Commerce findCommerceByIdOrThrowException(UUID commerceId) {
        return commerceRepository.findByCommerceIdAndDeletedFalse(commerceId)
                .orElseThrow(() -> new ResourceNotFoundException("Commerce", "id", commerceId));
    }

    @Override
    public void validateCommerceOwnership(UUID commerceId) {
        Commerce commerce = findCommerceByIdOrThrowException(commerceId);
        SecurityUtils.validateOwnership(commerce.getUser().getEmail());
    }

    @Override
    public void ifCommerceNameAlreadyExistsThrowException(String name) {
        if (commerceRepository.existsByNameAndDeletedFalse(name)) {
            throw new DuplicateResourceException("Commerce", "name");
        }
    }

    @Override
    public List<CommerceType> getOrCreateCommerceTypes(List<CommerceTypeEnum> commerceTypeEnums) {
        return commerceTypeEnums.stream()
                .map(this::getOrCreateCommerceType)
                .collect(Collectors.toList());
    }

    @Override
    public void validateAtLeastOneFieldToUpdate(UpdateCommerceRequest request) {
        boolean hasAtLeastOneField = StringUtils.hasText(request.getName()) ||
                StringUtils.hasText(request.getDescription()) ||
                (request.getCommerceTypes() != null && !request.getCommerceTypes().isEmpty()) ||
                StringUtils.hasText(request.getOpeningHours()) ||
                StringUtils.hasText(request.getAddress()) ||
                StringUtils.hasText(request.getLocality()) ||
                StringUtils.hasText(request.getPhone()) ||
                StringUtils.hasText(request.getEmail()) ||
                StringUtils.hasText(request.getPassword()) ||
                StringUtils.hasText(request.getConfirmPassword());

        if (!hasAtLeastOneField) {
            throw new ValidationException("Debe modificar al menos un campo");
        }
    }

    @Override
    public boolean validateAndProcessUpdate(User user, UpdateCommerceRequest request) {
        validateAtLeastOneFieldToUpdate(request);

        boolean emailChanged = userFacade.validateAndCheckEmailChange(user, request.getEmail());
        userFacade.validatePasswordsIfProvided(request.getPassword(), request.getConfirmPassword());

        return emailChanged;
    }

    @Override
    public void applyUserChanges(User user, UpdateCommerceRequest request, boolean emailChanged) {
        if (emailChanged) {
            user.setEmail(request.getEmail().trim());
            user.setEnabled(false);
            tokenService.saveUserToken(user);
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private CommerceType getOrCreateCommerceType(CommerceTypeEnum commerceTypeEnum) {
        return commerceTypeRepository.findByName(commerceTypeEnum)
                .orElseGet(() -> createNewCommerceType(commerceTypeEnum));
    }

    private CommerceType createNewCommerceType(CommerceTypeEnum commerceTypeEnum) {
        CommerceType newType = CommerceType.builder()
                .name(commerceTypeEnum)
                .build();
        return commerceTypeRepository.save(newType);
    }
}