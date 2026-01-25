package com.rescuebites.api.commerce.facades.interfaces;

import com.rescuebites.api.client.controllers.requests.UpdateClientRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.users.data.models.User;

import java.util.List;

public interface ICommerceFacade {

    void ifCommerceNameAlreadyExistsThrowException(String name);

    List<CommerceType> getOrCreateCommerceTypes(List<CommerceTypeEnum> commerceTypeEnums);

    void validateAtLeastOneFieldToUpdate(UpdateCommerceRequest request);

    boolean validateAndProcessUpdate(User user, UpdateCommerceRequest request);

    void applyUserChanges(User user, UpdateCommerceRequest request, boolean emailChanged);

}