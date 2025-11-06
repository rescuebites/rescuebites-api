package com.rescuebites.api.commerce.controllers.implementations;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.controllers.interfaces.IPackageController;
import com.rescuebites.api.commerce.controllers.responses.MomentaryPreferenceSearchResponse;
import com.rescuebites.api.commerce.services.interfaces.IPackageSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PackageControllerImpl implements IPackageController {

    private final IPackageSearchService packageSearchService;

    @Override
    public MomentaryPreferenceSearchResponse searchPackages(String query, List<PreferenceType> preferences) {
        return packageSearchService.searchPackages(query, preferences);
    }
}
