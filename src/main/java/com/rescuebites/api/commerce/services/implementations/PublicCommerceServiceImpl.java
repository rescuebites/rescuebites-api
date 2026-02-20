package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.IPublicCommerceService;
import com.rescuebites.api.exceptions.custom_exceptions.ResourceNotFoundException;
import com.rescuebites.api.security.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicCommerceServiceImpl implements IPublicCommerceService {

    private final ICommerceRepository commerceRepository;
    private final ICommerceFacade commerceFacade;

    @Override
    @Transactional
    public Page<CommercePublicResponse> getAllCommerces(Pageable pageable) {
        Page<Commerce> commerces = commerceRepository.findByDeletedFalse(pageable);
        return commerces.map(CommerceMapper::toCommercePublicResponse);
    }

    @Override
    @Transactional
    public CommerceResponse getCommerceById(UUID commerceId) {
        Commerce commerce = commerceFacade.findCommerceByIdOrThrowException(commerceId);

        return CommerceMapper.toCommerceResponse(commerce);
    }

    @Override
    @Transactional
    public Page<CommercePublicResponse> getCommercesByType(CommerceTypeEnum commerceType, Pageable pageable) {
        Page<Commerce> commerces = commerceRepository.findActiveByCommerceType(commerceType, pageable);
        return commerces.map(CommerceMapper::toCommercePublicResponse);
    }
}