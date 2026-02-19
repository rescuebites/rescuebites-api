package com.rescuebites.api.commerce.facades.interfaces;

import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.users.data.models.User;

import java.util.List;
import java.util.UUID;

public interface ICommerceFacade {

    Commerce findCommerceByIdOrThrowException(UUID commerceId);

    void validateCommerceOwnership(UUID commerceId);

    void ifCommerceNameAlreadyExistsThrowException(String name);

    List<CommerceType> getOrCreateCommerceTypes(List<CommerceTypeEnum> commerceTypeEnums);

    void validateAtLeastOneFieldToUpdate(UpdateCommerceRequest request);

    boolean validateAndProcessUpdate(User user, UpdateCommerceRequest request);

    void applyUserChanges(User user, UpdateCommerceRequest request, boolean emailChanged);

}