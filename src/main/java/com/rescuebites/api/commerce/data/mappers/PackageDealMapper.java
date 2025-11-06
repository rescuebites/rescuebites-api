package com.rescuebites.api.commerce.data.mappers;

import com.rescuebites.api.commerce.controllers.responses.PackageSummaryResponse;
import com.rescuebites.api.commerce.data.models.PackageDeal;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PackageDealMapper {

    public static PackageSummaryResponse toPackageSummaryResponse(PackageDeal packageDeal) {
        return new PackageSummaryResponse(
                packageDeal.getPackageId(),
                packageDeal.getName(),
                packageDeal.getDescription(),
                packageDeal.getCommerceId(),
                packageDeal.getApplicablePreferences().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
    }
}
