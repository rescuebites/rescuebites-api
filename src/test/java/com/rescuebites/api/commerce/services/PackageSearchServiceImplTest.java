package com.rescuebites.api.commerce.services;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.commerce.controllers.responses.MomentaryPreferenceSearchResponse;
import com.rescuebites.api.commerce.data.models.PackageDeal;
import com.rescuebites.api.commerce.repositories.IPackageDealRepository;
import com.rescuebites.api.commerce.services.implementations.PackageSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PackageSearchServiceImplTest {

    @Mock
    private IPackageDealRepository packageDealRepository;

    @InjectMocks
    private PackageSearchServiceImpl packageSearchService;

    private PackageDeal veganPackage;
    private PackageDeal saltyPackage;

    @BeforeEach
    void setUp() {
        veganPackage = PackageDeal.builder()
                .packageId(UUID.randomUUID())
                .name("Paquete Vegáno Delicioso")
                .description("Incluye opciones sin carne y sin lácteos")
                .commerceId(UUID.randomUUID())
                .applicablePreferences(List.of(PreferenceType.VEGAN, PreferenceType.CELIAC))
                .available(true)
                .deleted(false)
                .build();

        saltyPackage = PackageDeal.builder()
                .packageId(UUID.randomUUID())
                .name("Combo Salado clásico")
                .description("Ideal para quienes aman la cocina tradicional")
                .commerceId(UUID.randomUUID())
                .applicablePreferences(List.of(PreferenceType.LOW_SODIUM))
                .available(true)
                .deleted(false)
                .build();
    }

    @Test
    void givenQueryWithDifferentCaseAndAccents_whenSearch_thenReturnsMatchingPackages() {
        when(packageDealRepository.findByDeletedFalseAndAvailableTrue())
                .thenReturn(List.of(veganPackage, saltyPackage));

        MomentaryPreferenceSearchResponse response = packageSearchService.searchPackages("vegano", List.of());

        assertThat(response.results()).hasSize(1);
        assertThat(response.results().get(0).name()).contains("Vegáno");
        assertThat(response.message()).isNull();
    }

    @Test
    void givenMomentaryPreferences_whenSearch_thenFiltersByPreferences() {
        when(packageDealRepository.findByDeletedFalseAndAvailableTrue())
                .thenReturn(List.of(veganPackage, saltyPackage));

        MomentaryPreferenceSearchResponse response = packageSearchService.searchPackages("", List.of(PreferenceType.VEGAN));

        assertThat(response.results()).hasSize(1);
        assertThat(response.results().get(0).applicablePreferences())
                .containsExactlyInAnyOrder("VEGAN", "CELIAC");
    }

    @Test
    void givenNoMatches_whenSearch_thenReturnsMessage() {
        when(packageDealRepository.findByDeletedFalseAndAvailableTrue())
                .thenReturn(List.of(veganPackage, saltyPackage));

        MomentaryPreferenceSearchResponse response = packageSearchService.searchPackages("dulce", List.of());

        assertThat(response.results()).isEmpty();
        assertThat(response.message()).isEqualTo("No se encontraron resultados para tu búsqueda");
    }
}
