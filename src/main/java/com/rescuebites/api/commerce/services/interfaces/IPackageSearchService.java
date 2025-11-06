package com.rescuebites.api.commerce.services.interfaces;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.controllers.responses.MomentaryPreferenceSearchResponse;

import java.util.List;

public interface IPackageSearchService {

    MomentaryPreferenceSearchResponse searchPackages(String query, List<PreferenceType> momentaryPreferences);
}
