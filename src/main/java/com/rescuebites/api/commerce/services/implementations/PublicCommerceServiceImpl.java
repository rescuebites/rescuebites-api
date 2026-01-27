package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.IPublicCommerceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicCommerceServiceImpl implements IPublicCommerceService {

    private final ICommerceRepository commerceRepository;

    @Override
    @Transactional
    public Page<CommercePublicResponse> getCommercesByType(CommerceTypeEnum commerceType, Pageable pageable) {
        Page<Commerce> commerces = commerceRepository.findActiveByCommerceType(commerceType, pageable);
        return commerces.map(CommerceMapper::toCommercePublicResponse);
    }
}