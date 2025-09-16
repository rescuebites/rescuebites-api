package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.data.models.commerce;
import org.springframework.stereotype.Component;

@Component
public class CommercesMapper {
    //Convierte el Dto a una entidad
    public commerce toCommerce(RegisterCommerceRequest registerCommerceRequest){
        return commerce.builder()
                .commerceTypes(registerCommerceRequest.commerceTypes())
                .name(registerCommerceRequest.name())
                .phone(registerCommerceRequest.phone())
                .openingHours(registerCommerceRequest.openingHours())
                .imageUrl(registerCommerceRequest.imageUrl())
                .direction(registerCommerceRequest.direction())
                .build();
    }
}
