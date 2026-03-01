package com.rescuebites.api.commerce.facades.implementations;

import com.rescuebites.api.commerce.controllers.requests.BusinessHoursRequest;
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

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public Commerce findCommerceWithDetailsOrThrowException(UUID commerceId) {
        return commerceRepository.findByIdWithDetails(commerceId)
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
                (request.getBusinessHours() != null && !request.getBusinessHours().isEmpty()) ||
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
    public void validateBusinessHours(List<BusinessHoursRequest> businessHours, boolean requireAllDays) {
        if (businessHours == null || businessHours.isEmpty()) {
            return;
        }

        validateNoDuplicateDays(businessHours);

        if (requireAllDays) {
            validateAllDaysPresent(businessHours);
        }

        businessHours.forEach(this::validateSingleBusinessHours);
    }

    @Override
    public boolean validateAndProcessUpdate(User user, UpdateCommerceRequest request) {
        validateAtLeastOneFieldToUpdate(request);
        validateBusinessHours(request.getBusinessHours(), false);

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

    private void validateNoDuplicateDays(List<BusinessHoursRequest> businessHours) {
        Set<DayOfWeek> days = new HashSet<>();
        for (BusinessHoursRequest bh : businessHours) {
            if (!days.add(bh.getDayOfWeek())) {
                throw new ValidationException(
                        "El día " + bh.getDayOfWeek() + " está duplicado en los horarios de atención");
            }
        }
    }

    private void validateAllDaysPresent(List<BusinessHoursRequest> businessHours) {
        Set<DayOfWeek> providedDays = businessHours.stream()
                .map(BusinessHoursRequest::getDayOfWeek)
                .collect(Collectors.toSet());

        if (providedDays.size() != 7) {
            Set<DayOfWeek> missingDays = new HashSet<>(Set.of(DayOfWeek.values()));
            missingDays.removeAll(providedDays);

            throw new ValidationException(
                    "Debe definir el horario de todos los días de la semana. Faltan: " +
                            missingDays.stream()
                                    .map(DayOfWeek::name)
                                    .collect(Collectors.joining(", ")));
        }
    }

    private void validateSingleBusinessHours(BusinessHoursRequest bh) {
        String day = bh.getDayOfWeek().name();

        if (bh.isClosed()) {
            return;
        }

        // Si no está cerrado, openTime y closeTime son obligatorios
        if (bh.getOpenTime() == null || bh.getCloseTime() == null) {
            throw new ValidationException(
                    day + ": la hora de apertura y cierre son obligatorias cuando el comercio está abierto");
        }

        // openTime debe ser anterior a closeTime
        if (!bh.getOpenTime().isBefore(bh.getCloseTime())) {
            throw new ValidationException(
                    day + ": la hora de apertura debe ser anterior a la hora de cierre");
        }

        // Validaciones del turno tarde
        boolean hasAfternoonOpen = bh.getAfternoonOpenTime() != null;
        boolean hasAfternoonClose = bh.getAfternoonCloseTime() != null;

        if (hasAfternoonOpen != hasAfternoonClose) {
            throw new ValidationException(
                    day + ": debe definir ambos horarios del turno tarde (apertura y cierre) o ninguno");
        }

        if (hasAfternoonOpen) {
            // afternoonOpenTime debe ser posterior a closeTime
            if (!bh.getAfternoonOpenTime().isAfter(bh.getCloseTime())) {
                throw new ValidationException(
                        day + ": la apertura del turno tarde debe ser posterior al cierre del turno mañana");
            }

            // afternoonOpenTime debe ser anterior a afternoonCloseTime
            if (!bh.getAfternoonOpenTime().isBefore(bh.getAfternoonCloseTime())) {
                throw new ValidationException(
                        day + ": la apertura del turno tarde debe ser anterior al cierre del turno tarde");
            }
        }
    }
}