package com.rescuebites.api.commerce.services.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.controllers.responses.MomentaryPreferenceSearchResponse;
import com.rescuebites.api.commerce.controllers.responses.PackageSummaryResponse;
import com.rescuebites.api.commerce.data.mappers.PackageDealMapper;
import com.rescuebites.api.commerce.data.models.PackageDeal;
import com.rescuebites.api.commerce.repositories.IPackageDealRepository;
import com.rescuebites.api.commerce.services.interfaces.IPackageSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageSearchServiceImpl implements IPackageSearchService {

    private static final String NO_RESULTS_MESSAGE = "No se encontraron resultados para tu búsqueda";

    private final IPackageDealRepository packageDealRepository;

    @Override
    public MomentaryPreferenceSearchResponse searchPackages(String query, List<PreferenceType> momentaryPreferences) {

        List<PackageDeal> activePackages = packageDealRepository.findByDeletedFalseAndAvailableTrue();

        String normalizedQuery = normalize(query);
        Predicate<PackageDeal> textMatchesPredicate = buildTextMatchesPredicate(normalizedQuery);
        Predicate<PackageDeal> preferencesPredicate = buildPreferencesPredicate(momentaryPreferences);

        List<PackageSummaryResponse> results = activePackages.stream()
                .filter(textMatchesPredicate)
                .filter(preferencesPredicate)
                .map(PackageDealMapper::toPackageSummaryResponse)
                .collect(Collectors.toList());

        String message = results.isEmpty() ? NO_RESULTS_MESSAGE : null;

        return new MomentaryPreferenceSearchResponse(results, message);
    }

    private Predicate<PackageDeal> buildTextMatchesPredicate(String normalizedQuery) {
        if (!StringUtils.hasText(normalizedQuery)) {
            return packageDeal -> true;
        }
        return packageDeal -> containsNormalized(packageDeal.getName(), normalizedQuery)
                || containsNormalized(packageDeal.getDescription(), normalizedQuery);
    }

    private Predicate<PackageDeal> buildPreferencesPredicate(List<PreferenceType> momentaryPreferences) {
        if (CollectionUtils.isEmpty(momentaryPreferences)) {
            return packageDeal -> true;
        }
        return packageDeal -> packageDeal.getApplicablePreferences() != null
                && packageDeal.getApplicablePreferences().containsAll(momentaryPreferences);
    }

    private boolean containsNormalized(String value, String normalizedQuery) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return normalize(value).contains(normalizedQuery);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }
}
