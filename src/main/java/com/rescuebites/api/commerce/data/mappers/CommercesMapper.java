package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.commerce.controllers.requests.RegisterCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceProfileResponse;
import com.rescuebites.api.commerce.data.models.CommerceType;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.users.data.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommercesMapper {
    //Convierte el Dto a una entidad
    public static Commerce toCommerce(RegisterCommerceRequest registerCommerceRequest, List<CommerceType> commerceTypes, User user, Image image){
        return Commerce.builder()
                .commerceTypes(commerceTypes)
                .name(registerCommerceRequest.name())
                .phone(registerCommerceRequest.phone())
                .openingHours(registerCommerceRequest.openingHours())
                .image(image)
                .direction(registerCommerceRequest.direction())
                .user(user)
                .build();
    }

//    public static CommerceProfileResponse toCommerceProfileResponse(Commerce commerce){
//        return new CommerceProfileResponse(commerce.getCommerceId(),
//                commerce.getUser().getUserId(),
//                commerce.getName(),
//                commerce.getDescription(),
//                commerce.getCommerceTypes(),
//                commerce.getOpeningHours(),
//                commerce.getDirection(),
//                commerce.getPhone(),
//                //commerce.getImage(),
//                //commerce.isActive()
//        );
//    }
}
