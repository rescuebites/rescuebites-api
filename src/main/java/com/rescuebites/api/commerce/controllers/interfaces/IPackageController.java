package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.controllers.responses.MomentaryPreferenceSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/api/v1/packages")
public interface IPackageController {

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    MomentaryPreferenceSearchResponse searchPackages(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "preferences", required = false) List<PreferenceType> preferences);
}
