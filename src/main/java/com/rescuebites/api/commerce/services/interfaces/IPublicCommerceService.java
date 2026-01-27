package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPublicCommerceService {
    Page<CommercePublicResponse> getCommercesByType(CommerceTypeEnum commerceType, Pageable pageable);
}