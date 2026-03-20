package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.commerce.controllers.responses.CommercePublicResponse;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import com.rescuebites.api.commerce.data.enums.CommerceTypeEnum;
import com.rescuebites.api.commerce.data.mappers.CommerceMapper;
import com.rescuebites.api.commerce.data.models.Commerce;
import com.rescuebites.api.commerce.facades.interfaces.ICommerceFacade;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.commerce.services.interfaces.IPublicCommerceService;
import com.rescuebites.api.shared.utils.SearchUtils;
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
    public Page<CommercePublicResponse> getAllCommerces(String locality, Pageable pageable) {
        String normalizedLocality = SearchUtils.normalizeQuery(locality);
        Page<Commerce> commerces = commerceRepository.findByDeletedFalseAndLocalityIgnoreCase(normalizedLocality, pageable);
        return commerces.map(CommerceMapper::toCommercePublicResponse);
    }

    @Override
    @Transactional
    public CommerceResponse getCommerceById(UUID commerceId) {
        Commerce commerce = commerceFacade.findCommerceByIdIncludingDeletedOrThrowException(commerceId);
        return CommerceMapper.toCommerceResponse(commerce);
    }

    @Override
    @Transactional
    public Page<CommercePublicResponse> getCommercesByType(CommerceTypeEnum commerceType, String locality, Pageable pageable) {
        String normalizedLocality = SearchUtils.normalizeQuery(locality);
        Page<Commerce> commerces = commerceRepository.findActiveByCommerceTypeAndLocality(commerceType, normalizedLocality, pageable);
        return commerces.map(CommerceMapper::toCommercePublicResponse);
    }
}